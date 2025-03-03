package com.share.external.lib.mvvm.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

fun <T : R, R> ViewModel.collectAsState(flow: Flow<T>, initial: T): State<R> {
    return flow.collectAsState(initial, viewModelScope)
}

fun <T : R, R> ViewModel.collectAsState(flow: Flow<T>): State<R?> {
    return flow.collectAsState(null, viewModelScope)
}

fun <T> ViewModel.collectAsState(flow: StateFlow<T>): State<T> {
    return flow.collectAsState(flow.value, viewModelScope, policy = referentialEqualityPolicy())
}

fun <T, R> ViewModel.collectAsState(flow: StateFlow<T>, transform: (T) -> R): State<R> {
    return flow.map(transform).collectAsState(transform(flow.value), viewModelScope)
}

fun <T> ViewModel.collectAsMutableState(flow: MutableStateFlow<T>): MutableState<T> {
    return MutableStateFlowState(flow, viewModelScope)
}

fun <T : R, R> Flow<T>.collectAsState(
    initial: T,
    coroutineScope: CoroutineScope,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy()
): State<R> {
    val state = mutableStateOf(initial, policy = policy)
    coroutineScope
        .launch {
            collect {
                state.value = it
            }
        }
    return state
}

fun <T> StateFlow<T>.collectAsState(coroutineScope: CoroutineScope): State<T> {
    return collectAsState(value, coroutineScope, policy = referentialEqualityPolicy())
}

fun <T> MutableStateFlow<T>.collectAsMutableState(coroutineScope: CoroutineScope): MutableState<T> {
    return MutableStateFlowState(this, coroutineScope)
}

private class MutableStateFlowState<T>(
    private val flow: MutableStateFlow<T>,
    coroutineScope: CoroutineScope,
) : MutableState<T> {
    private val state: State<T> = flow.collectAsState(coroutineScope)

    override var value: T
        get() = state.value
        set(value) {
            flow.value = value
        }

    override fun component1(): T {
        return value
    }

    override fun component2(): (T) -> Unit {
        return {
            value = it
        }
    }
}