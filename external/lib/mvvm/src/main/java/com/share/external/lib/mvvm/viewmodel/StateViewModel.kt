package com.share.external.lib.mvvm.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.css.android.compose.runtime.LoggingStateChangeObserver
import com.css.android.compose.runtime.StateChangeObserver
import com.css.android.compose.runtime.collectAsStateObserving
import com.share.compose.runtime.collectAsMutableState
import com.share.compose.runtime.collectAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty

/**
 * A base [ViewModel] that integrates Compose state observation and flow collection
 * with automatic logging and lifecycle-aware tracking via [StateChangeObserver].
 *
 * This class conforms to [LoggingStateChangeObserver], enabling debug-friendly
 * logging of all state-bound properties (via `mutableStateObservingOf` or `derivedStateObservingOf`)
 * without requiring any extra boilerplate in subclasses.
 *
 * ### Key Features:
 * - Implements [StateChangeObserver] to hook into state change events.
 * - Automatically cancels [scope] when cleared.
 * - Provides helpers for collecting [Flow] and [StateFlow] into Compose `State` with or without logging.
 *
 * ### Usage:
 * ```kotlin
 * class MyViewModel : StateViewModel() {
 *     var counter by mutableIntStateObservingOf(0)
 *
 *     val isLoading by someFlow.collectAsStateObserving(false)
 * }
 * ```
 *
 * ### Lifecycle Behavior:
 * This ViewModel automatically:
 * - Registers and deregisters from the [StateChangeObserverRegistry]
 * - Tracks each observed property by name
 * - Emits initial and changed values for each property via [LoggingStateChangeObserver]
 *
 * Subclasses can override [onInitialValue] or [onValueChanged] to customize behavior.
 *
 * @property scope A lifecycle-scoped [CoroutineScope] used for flow collection and async tasks.
 */
open class StateViewModel(
    val scope: CoroutineScope,
): LoggingStateChangeObserver {
    private val loggingDispatcher = Dispatchers.IO.limitedParallelism(1)

    override fun onInitialValue(
        instanceId: String,
        propertyName: String,
        value: Any?,
        state: Map<String, Any?>,
    ) {
        scope.launch(loggingDispatcher) {
            super.onInitialValue(
                instanceId = instanceId,
                propertyName = propertyName,
                value = value,
                state = state
            )
        }
    }

    override fun onValueChanged(
        instanceId: String,
        propertyName: String,
        value: Any?,
        state: Map<String, Any?>,
    ) {
        scope.launch(loggingDispatcher) {
            super.onValueChanged(
                instanceId = instanceId,
                propertyName = propertyName,
                value = value,
                state = state
            )
        }
    }

    override fun addCloseable(closeable: AutoCloseable) {
        scope.coroutineContext.job.invokeOnCompletion {
            closeable.close()
        }
    }

    fun <T : R, R> Flow<T>.collectAsState(initial: T): State<R> {
        return collectAsState(
            initial = initial,
            coroutineScope = scope
        )
    }

    fun <T : R, R> Flow<T>.collectAsState(): State<R?> {
        return collectAsState(
            initial = null,
            coroutineScope = scope
        )
    }

    fun <T : R, R> StateFlow<T>.collectAsState(): State<T> {
        return collectAsState(coroutineScope = scope)
    }

    fun <T, R> StateFlow<T>.collectAsState(transform: (T) -> R): State<R> {
        return map(transform).collectAsState(
            initial = transform(value),
            coroutineScope = scope
        )
    }

    fun <T : R, R> Flow<T>.collectAsStateObserving(initial: T): ReadOnlyProperty<StateChangeObserver, R> {
        return collectAsStateObserving(
            initial = initial,
            coroutineScope = scope
        )
    }

    fun <T : R, R> Flow<T>.collectAsStateObserving(): ReadOnlyProperty<StateChangeObserver, R?> {
        return collectAsStateObserving(
            initial = null,
            coroutineScope = scope
        )
    }

    fun <T : R, R> StateFlow<T>.collectAsStateObserving(): ReadOnlyProperty<StateChangeObserver, R> {
        return collectAsStateObserving(coroutineScope = scope)
    }

    fun <T, R> StateFlow<T>.collectAsStateObserving(transform: (T) -> R): ReadOnlyProperty<StateChangeObserver, R> {
        return map(transform).collectAsStateObserving(
            initial = transform(value),
            coroutineScope = scope
        )
    }

    fun <T> MutableStateFlow<T>.collectAsMutableState(): MutableState<T> {
        return collectAsMutableState(coroutineScope = scope)
    }
}
