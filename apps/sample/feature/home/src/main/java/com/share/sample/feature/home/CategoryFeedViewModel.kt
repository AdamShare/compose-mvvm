package com.share.sample.feature.home

import com.share.external.foundation.coroutines.CoroutineScopeFactory
import com.share.external.lib.compose.state.ViewModel
import com.share.sample.core.data.model.Category
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for a single category feed.
 */
sealed interface CategoryFeedState {
    data object Loading : CategoryFeedState
    data class Success(val items: List<FeedItem>) : CategoryFeedState
    data class Error(val message: String) : CategoryFeedState
}

/**
 * ViewModel for a single category's feed.
 *
 * Each category gets its own instance of this ViewModel, which loads
 * and maintains the feed data for that specific category.
 */
class CategoryFeedViewModel(
    scopeFactory: CoroutineScopeFactory,
    private val category: Category,
    private val feedRepository: FeedRepository
) : ViewModel(name = "CategoryFeedViewModel-${category.name}", scopeFactory = scopeFactory) {

    private val _state = MutableStateFlow<CategoryFeedState>(CategoryFeedState.Loading)
    val state: StateFlow<CategoryFeedState> = _state.asStateFlow()

    fun loadFeed() {
        scope.launch {
            feedRepository.getFeed(category)
                .onSuccess { items ->
                    _state.value = CategoryFeedState.Success(items)
                }
                .onFailure { error ->
                    if (_state.value !is CategoryFeedState.Success) {
                        _state.value = CategoryFeedState.Error(
                            error.message ?: "Failed to load feed"
                        )
                    }
                }
        }
    }

    fun refresh() {
        _state.value = CategoryFeedState.Loading
        loadFeed()
    }
}
