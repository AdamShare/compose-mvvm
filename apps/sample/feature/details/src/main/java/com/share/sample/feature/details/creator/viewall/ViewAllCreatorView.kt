package com.share.sample.feature.details.creator.viewall

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.share.external.lib.navigation.stack.NavigationRoute
import com.share.external.lib.navigation.stack.NavigationStack
import com.share.external.lib.navigation.stack.Screen
import com.share.external.lib.navigation.stack.toNavigationRoute
import com.share.external.lib.view.View
import com.share.external.lib.view.ViewProvider
import com.share.sample.core.data.api.ArtistResult
import com.share.sample.core.data.model.Creator
import com.share.sample.core.data.model.FeedItem
import com.share.sample.feature.details.DetailsComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object ViewAllCreatorViewModule {
    @ViewAllCreatorScope
    @Provides
    fun viewAllCreatorViewProvider(
        dependency: ViewAllCreatorComponent.Dependency,
        detailsFactory: DetailsComponent.Factory,
    ) = ViewAllCreatorViewProvider(
        creator = dependency.creator,
        detailsRouteFactory = { feedItem ->
            detailsFactory.toNavigationRoute { scope ->
                DetailsComponent.Dependency(
                    navigationScope = scope,
                    feedItem = feedItem,
                    mediaType = feedItem.mediaType
                )
            }
        },
        items = dependency.items,
        navigationStack = dependency.navigationScope,
    )
}

class ViewAllCreatorViewProvider(
    private val creator: Creator,
    private val detailsRouteFactory: (FeedItem) -> NavigationRoute<Screen>,
    private val items: List<ArtistResult>,
    private val navigationStack: NavigationStack<Screen>,
) : Screen {
    override fun onViewAppear(scope: CoroutineScope) = View {
        ViewAllCreatorContent(
            creatorName = creator.name,
            items = items,
            onBackClick = { navigationStack.pop() },
            onItemClick = { feedItem ->
                navigationStack.push(detailsRouteFactory(feedItem))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewAllCreatorContent(
    creatorName: String,
    items: List<ArtistResult>,
    onBackClick: () -> Unit,
    onItemClick: (FeedItem) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "All by $creatorName",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            items(items) { item ->
                val feedItem = item.toFeedItem()
                ViewAllItemCard(
                    item = item,
                    onClick = if (feedItem != null) {
                        { onItemClick(feedItem) }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun ViewAllItemCard(
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
