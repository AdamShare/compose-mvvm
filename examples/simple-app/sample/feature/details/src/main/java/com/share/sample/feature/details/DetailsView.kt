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
import com.getbackcompose.compose.runtime.collectAsState
import com.getbackcompose.navigation.stack.NavigationRoute
import com.getbackcompose.navigation.stack.NavigationStack
import com.getbackcompose.navigation.stack.Screen
import com.getbackcompose.core.View
import com.share.sample.core.data.model.Creator
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.model.Genre
import com.share.sample.core.data.model.MediaType
import com.share.sample.core.data.repository.FavoritesRepository
import com.share.sample.core.data.repository.FeedRepository
import com.share.sample.feature.details.creator.CreatorModalViewProvider
import com.share.sample.feature.details.creator.CreatorModalViewModel
import com.share.sample.feature.details.creator.viewall.ViewAllCreatorViewProvider
import com.share.sample.feature.details.genre.GenreViewProvider
import kotlinx.coroutines.CoroutineScope

/**
 * Routes for the details navigation.
 */
enum class DetailsRoute : com.getbackcompose.core.ViewKey {
    CreatorModal,
    Genre
}

class DetailsViewProvider(
    private val feedItem: FeedItem,
    private val mediaType: MediaType,
    private val navigationStack: NavigationStack<Screen>,
    private val favoritesRepository: FavoritesRepository,
    private val feedRepository: FeedRepository,
    private val managedScope: com.getbackcompose.foundation.coroutines.ManagedCoroutineScope,
) : Screen {
    override fun onViewAppear(scope: CoroutineScope): View {
        val viewModel = DetailsViewModel(
            scopeFactory = managedScope,
            feedItem = feedItem,
            mediaType = mediaType,
            favoritesRepository = favoritesRepository
        )

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
                    navigationStack.push(
                        NavigationRoute(
                            key = DetailsRoute.CreatorModal,
                            factory = { navScope ->
                                val creatorViewModel = CreatorModalViewModel(
                                    scopeFactory = navScope,
                                    creator = creator,
                                    mediaType = mediaType,
                                    feedRepository = feedRepository
                                )
                                CreatorModalViewProvider(
                                    navigationStack = navScope,
                                    feedRepository = feedRepository,
                                    favoritesRepository = favoritesRepository,
                                    managedScope = navScope,
                                    viewModel = creatorViewModel
                                )
                            }
                        )
                    )
                },
                onGenreClick = { genre ->
                    navigationStack.push(
                        NavigationRoute(
                            key = DetailsRoute.Genre,
                            factory = { navScope ->
                                GenreViewProvider(
                                    navigationStack = navScope,
                                    genre = genre
                                )
                            }
                        )
                    )
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
