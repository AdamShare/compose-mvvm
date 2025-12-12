package com.share.sample.feature.details.creator

import com.share.external.foundation.coroutines.CoroutineScopeFactory
import com.share.external.lib.compose.state.ViewModel
import com.share.sample.core.data.api.ArtistResult
import com.share.sample.core.data.model.Creator
import com.share.sample.core.data.model.MediaType
import com.share.sample.core.data.repository.FeedRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for the creator modal.
 */
sealed interface CreatorModalState {
    data object Loading : CreatorModalState
    data class Success(val items: List<ArtistResult>) : CreatorModalState
    data class Error(val message: String) : CreatorModalState
}

@Module
object CreatorModalViewModelModule {
    @CreatorModalScope
    @Provides
    fun viewModel(dependency: CreatorModalComponent.Dependency, feedRepository: FeedRepository) =
        CreatorModalViewModel(
            scopeFactory = dependency.navigationScope,
            creator = dependency.creator,
            mediaType = dependency.mediaType,
            feedRepository = feedRepository
        )
}

/**
 * ViewModel for the creator modal.
 *
 * Fetches and exposes other works by the same creator.
 */
class CreatorModalViewModel(
    scopeFactory: CoroutineScopeFactory,
    val creator: Creator,
    val mediaType: MediaType,
    private val feedRepository: FeedRepository
) : ViewModel(name = TAG, scopeFactory = scopeFactory) {

    private val _state = MutableStateFlow<CreatorModalState>(CreatorModalState.Loading)
    val state: StateFlow<CreatorModalState> = _state.asStateFlow()

    /**
     * Loads the creator's other works.
     * Does not set loading state to avoid flashing UI on refresh.
     */
    fun load() {
        scope.launch {
            feedRepository.getItemsByCreator(creator, mediaType)
                .onSuccess { items ->
                    _state.value = CreatorModalState.Success(items)
                }
                .onFailure { error ->
                    // Only show error if we don't already have data
                    if (_state.value !is CreatorModalState.Success) {
                        _state.value = CreatorModalState.Error(
                            error.message ?: "Failed to load creator info"
                        )
                    }
                }
        }
    }

    companion object {
        private const val TAG = "CreatorModalViewModel"
    }
}
