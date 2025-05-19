package com.share.external.lib.mvvm.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.share.compose.runtime.StateChangeObserver
import com.share.compose.runtime.collectAsStateObserving
import com.share.compose.runtime.collectAsMutableState
import com.share.compose.runtime.collectAsState
import com.share.external.foundation.coroutines.CoroutineScopeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlin.properties.ReadOnlyProperty

interface ScopedStateProvider: CoroutineScopeProvider {
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