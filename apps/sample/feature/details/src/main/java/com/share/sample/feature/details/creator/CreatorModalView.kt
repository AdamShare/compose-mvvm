package com.share.sample.feature.details.creator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.share.external.lib.compose.runtime.collectAsState
import com.share.external.lib.navigation.stack.NavigationRoute
import com.share.external.lib.navigation.stack.NavigationStack
import com.share.external.lib.navigation.stack.Screen
import com.share.external.lib.navigation.stack.toNavigationRoute
import com.share.external.lib.view.View
import com.share.external.lib.view.ViewPresentation
import com.share.sample.core.data.api.ArtistResult
import com.share.sample.core.data.model.Creator
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.model.MediaType
import com.share.sample.feature.details.DetailsComponent
import com.share.sample.feature.details.creator.viewall.ViewAllCreatorComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object CreatorModalViewModule {
    @CreatorModalScope
    @Provides
    fun creatorModalViewProvider(
        dependency: CreatorModalComponent.Dependency,
        detailsFactory: DetailsComponent.Factory,
        viewModel: CreatorModalViewModel,
        viewAllFactory: ViewAllCreatorComponent.Factory
    ) = CreatorModalViewProvider(
        detailsRouteFactory = { feedItem, mediaType ->
            detailsFactory.toNavigationRoute { scope ->
                DetailsComponent.Dependency(
                    navigationScope = scope,
                    feedItem = feedItem,
                    mediaType = mediaType
                )
            }
        },
        navigationStack = dependency.navigationScope,
        viewModel = viewModel,
        viewAllRouteFactory = { creator, items, mediaType ->
            viewAllFactory.toNavigationRoute { scope ->
                ViewAllCreatorComponent.Dependency(
                    navigationScope = scope,
                    creator = creator,
                    items = items,
                    mediaType = mediaType,
                )
            }
        },
    )
}

class CreatorModalViewProvider(
    private val detailsRouteFactory: (FeedItem, MediaType) -> NavigationRoute<Screen>,
    private val navigationStack: NavigationStack<Screen>,
    private val viewModel: CreatorModalViewModel,
    private val viewAllRouteFactory: (Creator, List<ArtistResult>, MediaType) -> NavigationRoute<Screen>,
) : Screen {
    override val preferredPresentationStyle: @Composable () -> ViewPresentation.Style = {
        ViewPresentation.Style.Modal()
    }

    override fun onViewAppear(scope: CoroutineScope): View {
        viewModel.load()

        val state by viewModel.state.collectAsState(scope)

        return View {
            CreatorModalContent(
                creatorName = viewModel.creator.name,
                state = state,
                onDismiss = { navigationStack.pop() },
                onItemClick = { feedItem ->
                    navigationStack.push(detailsRouteFactory(feedItem, viewModel.mediaType))
                },
                onViewAllClick = { items ->
                    navigationStack.push(viewAllRouteFactory(viewModel.creator, items, viewModel.mediaType))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatorModalContent(
    creatorName: String,
    state: CreatorModalState,
    onDismiss: () -> Unit,
    onItemClick: (FeedItem) -> Unit,
    onViewAllClick: (List<ArtistResult>) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "More by $creatorName",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                is CreatorModalState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is CreatorModalState.Success -> {
                    if (state.items.isEmpty()) {
                        Text(
                            text = "No other items found",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        val previewItems = state.items.take(PREVIEW_ITEM_COUNT)
                        val hasMoreItems = state.items.size > PREVIEW_ITEM_COUNT

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(previewItems) { item ->
                                val feedItem = item.toFeedItem()
                                CreatorItemCard(
                                    item = item,
                                    onClick = if (feedItem != null) {
                                        { onItemClick(feedItem) }
                                    } else null
                                )
                            }

                            if (hasMoreItems) {
                                item {
                                    ViewAllButton(
                                        itemCount = state.items.size,
                                        onClick = { onViewAllClick(state.items) }
                                    )
                                }
                            }
                        }
                    }
                }

                is CreatorModalState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Error loading creator info",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatorItemCard(
    item: ArtistResult,
    onClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item.artworkUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = item.displayName,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                item.primaryGenreName?.let { genre ->
                    Text(
                        text = genre,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ViewAllButton(
    itemCount: Int,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "View All ($itemCount items)",
                style = MaterialTheme.typography.labelLarge
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private const val PREVIEW_ITEM_COUNT = 5
