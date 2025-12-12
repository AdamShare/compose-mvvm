package com.share.sample.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.share.external.lib.compose.runtime.collectAsState
import com.share.external.lib.navigation.stack.NavigationRoute
import com.share.external.lib.navigation.stack.NavigationStack
import com.share.external.lib.navigation.stack.Screen
import com.share.external.lib.view.View
import com.share.external.lib.view.ViewProvider
import com.share.sample.core.data.model.FeedItem
import kotlinx.coroutines.CoroutineScope

/**
 * ViewProvider for a single category's feed.
 *
 * Each category gets its own instance with its own ViewModel,
 * preserving scroll position and loaded data when switching categories.
 */
class CategoryFeedViewProvider(
    private val navigationStack: NavigationStack<Screen>,
    private val detailsRouteFactory: (FeedItem) -> NavigationRoute<Screen>,
    private val viewModel: CategoryFeedViewModel,
) : ViewProvider {

    override fun onViewAppear(scope: CoroutineScope): View {
        viewModel.loadFeed()

        val state by viewModel.state.collectAsState(scope)

        return View {
            CategoryFeedContent(
                state = state,
                onItemClick = { item -> navigationStack.push(detailsRouteFactory(item)) },
                onRefresh = { viewModel.refresh() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFeedContent(
    state: CategoryFeedState,
    onItemClick: (FeedItem) -> Unit,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = state is CategoryFeedState.Loading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        when (state) {
            is CategoryFeedState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is CategoryFeedState.Success -> {
                if (state.items.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No items found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    val listState = rememberLazyListState()

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.items,
                            key = { it.id }
                        ) { item ->
                            FeedItemCard(
                                item = item,
                                onClick = { onItemClick(item) }
                            )
                        }
                    }
                }
            }

            is CategoryFeedState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error loading feed",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Button(
                        onClick = onRefresh,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = "Retry")
                    }
                }
            }
        }
    }
}
