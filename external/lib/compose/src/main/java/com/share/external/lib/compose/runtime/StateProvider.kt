package com.share.external.lib.compose.runtime

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.share.external.foundation.coroutines.CoroutineScopeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlin.properties.ReadOnlyProperty

/**
 * Provides convenient extension functions for converting [Flow] and [StateFlow] to Compose [State].
 *
 * This interface extends [CoroutineScopeProvider] and offers flow-to-state conversion utilities
 * that automatically use the provider's [scope] for collection. It simplifies ViewModel implementations
 * by eliminating the need to explicitly pass coroutine scopes to state conversion functions.
 *
 * ### Available Conversions
 * - **Flow → State**: Convert any [Flow] to a read-only [State] with an initial value
 * - **StateFlow → State**: Convert a [StateFlow] to [State], using the flow's current value as initial
 * - **MutableStateFlow → MutableState**: Two-way binding between flow and compose state
 * - **Observing variants**: Versions that integrate with [StateChangeObserver] for logging/debugging
 *
 * ### Usage
 * ```kotlin
 * class MyViewModel(repository: Repository) : StateProvider {
 *     override val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
 *
 *     // Automatically uses this.scope for collection
 *     val items: State<List<Item>> = repository.itemsFlow.collectAsState(emptyList())
 *     val selectedId: State<String?> = repository.selectedFlow.collectAsState()
 *     var filter by filterFlow.collectAsMutableState()
 * }
 * ```
 *
 * @see StateChangeObserver for observing state changes
 * @see collectAsState for the underlying state conversion
 */
interface StateProvider : CoroutineScopeProvider {
    fun <T : R, R> Flow<T>.collectAsState(initial: T): State<R> {
        return collectAsState(initial = initial, coroutineScope = scope)
    }

    fun <T : R, R> Flow<T>.collectAsState(): State<R?> {
        return collectAsState(initial = null, coroutineScope = scope)
    }

    fun <T : R, R> StateFlow<T>.collectAsState(): State<T> {
        return collectAsState(coroutineScope = scope)
    }

    fun <T, R> StateFlow<T>.collectAsState(transform: (T) -> R): State<R> {
        return map(transform).collectAsState(initial = transform(value), coroutineScope = scope)
    }

    fun <T : R, R> Flow<T>.collectAsStateObserving(initial: T): ReadOnlyProperty<StateChangeObserver, R> {
        return collectAsStateObserving(initial = initial, coroutineScope = scope)
    }

    fun <T : R, R> Flow<T>.collectAsStateObserving(): ReadOnlyProperty<StateChangeObserver, R?> {
        return collectAsStateObserving(initial = null, coroutineScope = scope)
    }

    fun <T : R, R> StateFlow<T>.collectAsStateObserving(): ReadOnlyProperty<StateChangeObserver, R> {
        return collectAsStateObserving(coroutineScope = scope)
    }

    fun <T, R> StateFlow<T>.collectAsStateObserving(transform: (T) -> R): ReadOnlyProperty<StateChangeObserver, R> {
        return map(transform).collectAsStateObserving(initial = transform(value), coroutineScope = scope)
    }

    fun <T> MutableStateFlow<T>.collectAsMutableState(): MutableState<T> {
        return collectAsMutableState(coroutineScope = scope)
    }
}