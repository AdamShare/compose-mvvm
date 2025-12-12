package com.share.external.lib.compose.state

import com.share.external.lib.compose.runtime.LoggingStateChangeObserver
import com.share.external.foundation.coroutines.CoroutineScopeFactory
import com.share.external.lib.compose.runtime.StateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Base class for ViewModels that integrates with Compose state management and automatic state logging.
 *
 * This class combines:
 * - [StateProvider] for convenient Flow → State conversions
 * - [LoggingStateChangeObserver] for automatic state change logging during development
 *
 * ### Features
 * - All state changes are automatically logged via [LoggingStateChangeObserver]
 * - Flow-to-State conversions use the ViewModel's scope automatically
 * - Resources registered via [addCloseable] are cleaned up when the scope cancels
 *
 * ### Usage
 * ```kotlin
 * class MyViewModel(repository: Repository, scope: CoroutineScopeFactory) : ViewModel("MyViewModel", scope) {
 *     // State from flows - automatically collected in this.scope
 *     val items: State<List<Item>> = repository.itemsFlow.collectAsState(emptyList())
 *
 *     // Observable mutable state - changes are logged automatically
 *     var isLoading by mutableStateObservingOf(false)
 *     var selectedIndex by mutableIntStateObservingOf(0)
 *
 *     // Derived state - computed values that update when dependencies change
 *     val hasSelection by derivedStateObservingOf { selectedIndex >= 0 }
 * }
 * ```
 *
 * @param scope The [CoroutineScope] that owns this ViewModel's lifecycle.
 *
 * @see StateProvider for flow-to-state conversion utilities
 * @see LoggingStateChangeObserver for state change logging
 */
open class ViewModel(override val scope: CoroutineScope) : StateProvider, LoggingStateChangeObserver {
    /**
     * Creates a ViewModel with a named scope created from a [CoroutineScopeFactory].
     *
     * The scope is created on [Dispatchers.Main.immediate] for safe UI state updates.
     *
     * @param name A human-readable name for the scope, useful for debugging and logging.
     * @param scopeFactory Factory for creating the underlying coroutine scope.
     */
    constructor(
        name: String,
        scopeFactory: CoroutineScopeFactory,
    ) : this(scope = scopeFactory.create(name = name, context = Dispatchers.Main.immediate))
}