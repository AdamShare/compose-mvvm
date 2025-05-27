package com.share.external.lib.mvvm.viewmodel

import androidx.compose.runtime.State
import com.share.compose.runtime.StateChangeObserver
import com.share.compose.runtime.collectAsState
import com.share.compose.runtime.collectAsStateObserving
import com.share.external.foundation.coroutines.CoroutineScopeProvider
import com.share.external.lib.mvvm.navigation.lifecycle.ViewLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlin.properties.ReadOnlyProperty

interface ViewStateProvider: CoroutineScopeProvider, ViewLifecycle {
    fun <T : R, R> Flow<T>.collectAsStateWhileVisible(initial: T): State<R> {
        return collectAsState(initial = initial, coroutineScope = scope, active = viewAppearanceEvents)
    }

    fun <T : R, R> Flow<T>.collectAsStateWhileVisible(): State<R?> {
        return collectAsState(initial = null, coroutineScope = scope, active = viewAppearanceEvents)
    }

    fun <T : R, R> StateFlow<T>.collectAsStateWhileVisible(): State<T> {
        return collectAsState(coroutineScope = scope, active = viewAppearanceEvents)
    }

    fun <T, R> StateFlow<T>.collectAsStateWhileVisible(transform: (T) -> R): State<R> {
        return map(transform).collectAsState(initial = transform(value), coroutineScope = scope, active = viewAppearanceEvents)
    }

    fun <T : R, R> Flow<T>.collectAsStateWhileVisibleObserving(initial: T): ReadOnlyProperty<StateChangeObserver, R> {
        return collectAsStateObserving(initial = initial, coroutineScope = scope, active = viewAppearanceEvents)
    }

    fun <T : R, R> Flow<T>.collectAsStateWhileVisibleObserving(): ReadOnlyProperty<StateChangeObserver, R?> {
        return collectAsStateObserving(initial = null, coroutineScope = scope, active = viewAppearanceEvents)
    }

    fun <T : R, R> StateFlow<T>.collectAsStateWhileVisibleObserving(): ReadOnlyProperty<StateChangeObserver, R> {
        return collectAsStateObserving(coroutineScope = scope, active = viewAppearanceEvents)
    }

    fun <T, R> StateFlow<T>.collectAsStateWhileVisibleObserving(transform: (T) -> R): ReadOnlyProperty<StateChangeObserver, R> {
        return map(transform).collectAsStateObserving(initial = transform(value), coroutineScope = scope, active = viewAppearanceEvents)
    }
}