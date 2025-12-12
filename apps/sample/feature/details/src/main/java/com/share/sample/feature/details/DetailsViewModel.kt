package com.share.sample.feature.details

import com.share.external.foundation.coroutines.CoroutineScopeFactory
import com.share.external.lib.compose.state.ViewModel
import com.share.sample.core.data.model.Creator
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.model.Genre
import com.share.sample.core.data.model.MediaType
import com.share.sample.core.data.repository.FavoritesRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Module
object DetailsViewModelModule {
    @DetailsScope
    @Provides
    fun viewModel(dependency: DetailsComponent.Dependency, favoritesRepository: FavoritesRepository) =
        DetailsViewModel(
            scopeFactory = dependency.navigationScope,
            feedItem = dependency.feedItem,
            mediaType = dependency.mediaType,
            favoritesRepository = favoritesRepository
        )
}

/**
 * ViewModel for the details screen.
 *
 * Provides the feed item data and favorite toggle functionality.
 */
class DetailsViewModel(
    scopeFactory: CoroutineScopeFactory,
    val feedItem: FeedItem,
    val mediaType: MediaType,
    private val favoritesRepository: FavoritesRepository
) : ViewModel(name = TAG, scopeFactory = scopeFactory) {

    /**
     * Returns a StateFlow indicating if this item is a favorite.
     */
    fun isFavorite(): StateFlow<Boolean> =
        favoritesRepository.favorites
            .map { favorites -> favorites.contains(feedItem.id) }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * Toggles the favorite status of this item.
     */
    fun toggleFavorite() {
        favoritesRepository.toggleFavorite(feedItem.id)
    }

    /**
     * Returns the creator info if available.
     */
    val creator: Creator?
        get() = feedItem.artistId?.let { artistId ->
            Creator(id = artistId, name = feedItem.artistName)
        }

    /**
     * Returns the genres for this item.
     */
    val genres: List<Genre> = feedItem.genres

    companion object {
        private const val TAG = "DetailsViewModel"
    }
}
