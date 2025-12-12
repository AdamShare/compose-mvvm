package com.share.sample.feature.favorites

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.compose.runtime.StateProvider
import com.share.external.lib.compose.runtime.collectAsState
import com.share.external.lib.navigation.stack.ModalNavigationStack
import com.share.external.lib.navigation.stack.NavigationRoute
import com.share.external.lib.navigation.stack.NavigationStack
import com.share.external.lib.navigation.stack.NavigationStackEntry
import com.share.external.lib.navigation.stack.NavigationStackHost
import com.share.external.lib.navigation.stack.Screen
import com.share.external.lib.navigation.stack.toNavigationRoute
import com.share.external.lib.view.View
import com.share.external.lib.view.ViewProvider
import com.share.sample.core.data.model.FeedItem
import com.share.sample.feature.details.DetailsComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object FavoritesViewModule {
    @FavoritesScope
    @Provides
    fun favoritesViewProvider(
        dependency: FavoritesComponent.Dependency,
        detailsFactory: DetailsComponent.Factory,
        viewModel: FavoritesViewModel,
    ) = FavoritesViewProvider(
        detailsFactory = { feedItem ->
            detailsFactory.toNavigationRoute { scope ->
                DetailsComponent.Dependency(
                    navigationScope = scope,
                    feedItem = feedItem,
                    mediaType = viewModel.getMediaType(feedItem)
                )
            }
        },
        scope = dependency.scope,
        viewModel = viewModel,
        )
}

class FavoritesViewProvider(
    private val detailsFactory: (FeedItem) -> NavigationRoute<Screen>,
    scope: ManagedCoroutineScope,
    private val viewModel: FavoritesViewModel,
) : ViewProvider {
    private val navigationStack = ModalNavigationStack<Screen>(
        rootScope = scope,
    )
    private val root = navigationStack.rootNavigationScope()

    override fun onViewAppear(scope: CoroutineScope): View {
        val state by viewModel.state.collectAsState(scope)

        return View {
            NavigationStackHost(
                name = "FavoritesNavigationStackHost",
                backHandlerEnabled = navigationStack.size > 1,
                stack = navigationStack
            ) {
                FavoritesContent(
                    state = state,
                    onItemClick = { feedItem ->
                        root.push(detailsFactory(feedItem))
                    },
                    onRemoveClick = { itemId ->
                        viewModel.removeFavorite(itemId)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesContent(
    state: FavoritesState,
    onItemClick: (FeedItem) -> Unit,
    onRemoveClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Favorites") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                is FavoritesState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is FavoritesState.Success -> {
                    if (state.items.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No favorites yet",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "Browse items and tap the heart to add favorites",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.items,
                                key = { it.id }
                            ) { favoriteItem ->
                                FavoriteItemCard(
                                    favoriteItem = favoriteItem,
                                    onClick = {
                                        favoriteItem.feedItem?.let { onItemClick(it) }
                                    },
                                    onRemoveClick = { onRemoveClick(favoriteItem.id) }
                                )
                            }
                        }
                    }
                }

                is FavoritesState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error loading favorites",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteItemCard(
    favoriteItem: FavoriteItem,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val feedItem = favoriteItem.feedItem

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = if (feedItem != null) onClick else ({})
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Artwork
            if (feedItem != null) {
                AsyncImage(
                    model = feedItem.artworkUrl100,
                    contentDescription = feedItem.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (feedItem != null) {
                    Text(
                        text = feedItem.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = feedItem.artistName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = "ID: ${favoriteItem.id}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Remove button
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove from favorites",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
