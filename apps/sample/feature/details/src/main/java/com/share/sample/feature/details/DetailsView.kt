package com.share.sample.feature.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AssistChip
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
import com.share.external.lib.compose.runtime.collectAsState
import com.share.external.lib.navigation.stack.NavigationRoute
import com.share.external.lib.navigation.stack.NavigationStack
import com.share.external.lib.navigation.stack.Screen
import com.share.external.lib.navigation.stack.toNavigationRoute
import com.share.external.lib.view.View
import com.share.external.lib.view.ViewProvider
import com.share.sample.core.data.model.Creator
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.model.Genre
import com.share.sample.core.data.model.MediaType
import com.share.sample.feature.details.creator.CreatorModalComponent
import com.share.sample.feature.details.genre.GenreComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object DetailsViewModule {
    @DetailsScope
    @Provides
    fun detailsViewProvider(
        dependency: DetailsComponent.Dependency,
        viewModel: DetailsViewModel,
        creatorModalFactory: CreatorModalComponent.Factory,
        genreFactory: GenreComponent.Factory,
    ) = DetailsViewProvider(
        navigationStack = dependency.navigationScope,
        viewModel = viewModel,
        creatorModalRouteFactory = { creator, mediaType ->
            creatorModalFactory.toNavigationRoute { scope ->
                CreatorModalComponent.Dependency(
                    navigationScope = scope,
                    creator = creator,
                    mediaType = mediaType,
                )
            }
        },
        genreRouteFactory = { genre ->
            genreFactory.toNavigationRoute { scope ->
                GenreComponent.Dependency(
                    navigationScope = scope,
                    genre = genre
                )
            }
        },
    )
}

class DetailsViewProvider(
    private val creatorModalRouteFactory: (Creator, MediaType) -> NavigationRoute<Screen>,
    private val genreRouteFactory: (Genre) -> NavigationRoute<Screen>,
    private val navigationStack: NavigationStack<Screen>,
    private val viewModel: DetailsViewModel,
) : Screen {
    override fun onViewAppear(scope: CoroutineScope): View {
        val isFavorite by viewModel.isFavorite().collectAsState(scope)

        return View {
            DetailsContent(
                feedItem = viewModel.feedItem,
                isFavorite = isFavorite,
                creator = viewModel.creator,
                genres = viewModel.genres,
                onBackClick = { navigationStack.pop() },
                onFavoriteClick = { viewModel.toggleFavorite() },
                onCreatorClick = { creator ->
                    navigationStack.push(creatorModalRouteFactory(creator, viewModel.mediaType))
                },
                onGenreClick = { genre ->
                    navigationStack.push(genreRouteFactory(genre))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DetailsContent(
    feedItem: FeedItem,
    isFavorite: Boolean,
    creator: Creator?,
    genres: List<Genre>,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCreatorClick: (Creator) -> Unit,
    onGenreClick: (Genre) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = feedItem.name,
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
                },
                actions = {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Artwork + Title/Artist side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Artwork - fixed size for compact display
                AsyncImage(
                    model = feedItem.artworkUrl500,
                    contentDescription = feedItem.name,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                // Title and artist info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = feedItem.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (creator != null) {
                        Text(
                            text = creator.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onCreatorClick(creator) }
                        )
                    } else {
                        Text(
                            text = feedItem.artistName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Release date
                    feedItem.releaseDate?.let { releaseDate ->
                        Text(
                            text = releaseDate,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Genres
            if (genres.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Genres",
                        style = MaterialTheme.typography.titleMedium
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        genres.forEach { genre ->
                            AssistChip(
                                onClick = { onGenreClick(genre) },
                                label = { Text(text = genre.name) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
