package com.share.compose.runtime

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.structuralEqualityPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * Collects a [Flow] and exposes the latest emitted value as a Compose [State].
 *
 * This helper is designed for non-Composable, lifecycle-aware usage, typically in a [ViewModel].
 *
 * @param initial The initial value before any emissions.
 * @param coroutineScope The scope in which the flow will be collected.
 * @param policy The mutation policy used to detect equality between state updates.
 * @return A [State] that reflects the latest emitted value from the flow.
 */
fun <T : R, R> Flow<T>.collectAsState(
    initial: T,
    coroutineScope: CoroutineScope,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy()
): State<R> {
    val state = mutableStateOf(value = initial, policy = policy)
    coroutineScope.launch {
        collect { state.value = it }
    }
    return state
}

/**
 * Collects a [StateFlow] and exposes it as Compose [State], using [referentialEqualityPolicy].
 *
 * This is optimized for reference-based updates and avoids unnecessary recomposition when
 * the instance remains stable.
 *
 * @param coroutineScope The scope used to collect the flow.
 * @return A Compose [State] reflecting the current and future values of the [StateFlow].
 */
fun <T> StateFlow<T>.collectAsState(coroutineScope: CoroutineScope): State<T> {
    return collectAsState(
        initial = value,
        coroutineScope = coroutineScope,
        policy = referentialEqualityPolicy()
    )
}

/**
 * Binds a [MutableStateFlow] to a [MutableState] interface, enabling read/write interaction
 * in Compose-like patterns, including two-way bindings and `by` delegation.
 *
 * Writes to this delegate will update the backing flow directly.
 *
 * @param coroutineScope The scope used to observe flow emissions.
 * @return A [MutableState] that acts as a proxy to the [MutableStateFlow].
 */
fun <T> MutableStateFlow<T>.collectAsMutableState(coroutineScope: CoroutineScope): MutableState<T> {
    return MutableStateFlowState(
        flow = this,
        coroutineScope = coroutineScope
    )
}

/**
 * Internal proxy that adapts a [MutableStateFlow] to behave like a [MutableState].
 *
 * This provides seamless integration of flow-backed values into Compose-style `var` properties.
 * Changes to the delegate will write directly to the backing flow.
 *
 * @param flow The underlying [MutableStateFlow] to synchronize with.
 * @param coroutineScope The coroutine scope used to collect updates from the flow.
 */
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

    override fun component1(): T = value

    override fun component2(): (T) -> Unit = { value = it }
}