package com.share.sample.feature.favorites

import com.share.external.foundation.coroutines.CoroutineScopeFactory
import com.share.external.lib.compose.state.ViewModel
import com.share.sample.core.data.model.Category
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.model.MediaType
import com.share.sample.core.data.repository.FavoritesRepository
import com.share.sample.core.data.repository.FeedRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * UI state for the favorites screen.
 */
sealed interface FavoritesState {
    data object Loading : FavoritesState
    data class Success(val items: List<FavoriteItem>) : FavoritesState
    data class Error(val message: String) : FavoritesState
}

/**
 * A favorite item with its associated feed data (if available).
 */
data class FavoriteItem(
    val id: String,
    val feedItem: FeedItem?
)

@Module
object FavoritesViewModelModule {
    @FavoritesScope
    @Provides
    fun viewModel(
        dependency: FavoritesComponent.Dependency,
        favoritesRepository: FavoritesRepository,
        feedRepository: FeedRepository
    ) = FavoritesViewModel(
        favoritesRepository = favoritesRepository,
        feedRepository = feedRepository,
        scopeFactory = dependency.scope,
    )
}

/**
 * ViewModel for the favorites screen.
 *
 * Observes the favorites repository and loads item details for display.
 */
class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val feedRepository: FeedRepository,
    scopeFactory: CoroutineScopeFactory,
) : ViewModel(name = TAG, scopeFactory = scopeFactory) {

    private val _state = MutableStateFlow<FavoritesState>(FavoritesState.Loading)
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    // Cache of loaded feed items by ID
    private val itemCache = mutableMapOf<String, FeedItem>()

    init {
        scope.launch {
            favoritesRepository.favorites.collectLatest { favoriteIds ->
                if (favoriteIds.isEmpty()) {
                    _state.value = FavoritesState.Success(emptyList())
                    return@collectLatest
                }

                // Check cache first, then load missing items
                val items = favoriteIds.map { id ->
                    FavoriteItem(
                        id = id,
                        feedItem = itemCache[id]
                    )
                }

                // Show cached items immediately (or keep showing previous data)
                _state.value = FavoritesState.Success(items)

                // Load missing items from feed (best effort)
                val missingIds = favoriteIds.filter { !itemCache.containsKey(it) }.toSet()
                if (missingIds.isNotEmpty()) {
                    loadMissingItems(missingIds, favoriteIds)
                }
            }
        }
    }

    private suspend fun loadMissingItems(
        missingIds: Set<String>,
        allFavoriteIds: Set<String>
    ) {
        // Try to find items in each category's feed
        Category.entries.forEach { category ->
            feedRepository.getFeed(category)
                .onSuccess { feedItems ->
                    feedItems.forEach { item ->
                        if (item.id in missingIds && !itemCache.containsKey(item.id)) {
                            itemCache[item.id] = item
                        }
                    }

                    // Update state with newly found items
                    val updatedItems = allFavoriteIds.map { id ->
                        FavoriteItem(
                            id = id,
                            feedItem = itemCache[id]
                        )
                    }
                    _state.value = FavoritesState.Success(updatedItems)
                }
        }
    }

    /**
     * Removes an item from favorites.
     */
    fun removeFavorite(itemId: String) {
        favoritesRepository.removeFavorite(itemId)
    }

    /**
     * Gets the media type for a feed item (used for navigation to details).
     */
    fun getMediaType(feedItem: FeedItem): MediaType {
        return feedItem.mediaType
    }

    companion object {
        private const val TAG = "FavoritesViewModel"
    }
}
