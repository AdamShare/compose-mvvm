package com.css.android.compose.runtime

import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.structuralEqualityPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Creates a derived [State] that observes changes and reports them via [StateChangeObserverRegistry].
 *
 * Designed to be used as a delegated property inside a [StateChangeObserver], this delegate
 * wraps [derivedStateOf] and lazily computes its value on first access.
 *
 * Each time the result changes (as defined by the [SnapshotMutationPolicy]), it triggers
 * [StateChangeObserver.onValueChanged] or [StateChangeObserver.onInitialValue].
 *
 * ### Example:
 * ```kotlin
 * val total by derivedStateObservingOf {
 *     subtotal.value + tax.value
 * }
 * ```
 *
 * @param policy Determines how to detect value changes (defaults to [structuralEqualityPolicy]).
 * @param producer Function that calculates the derived value.
 * @return A [ReadOnlyProperty] for use in delegation.
 *
 * @throws IllegalStateException if used outside a [StateChangeObserver] context.
 */
fun <T> derivedStateObservingOf(
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    producer: () -> T,
): ReadOnlyProperty<StateChangeObserver, T> = DerivedStateObserving(
    policy = policy,
    producer = producer
)

/**
 * Collects a [StateFlow] as an observing [State] delegate.
 *
 * Designed for use in classes that implement [StateChangeObserver], this utility
 * binds a [StateFlow] to a derived [State] and logs changes using [StateChangeObserverRegistry].
 *
 * This is similar to `collectAsState`, but safe for use in non-Composable code such as ViewModels.
 *
 * @param coroutineScope The scope in which the flow will be collected.
 * @return A [ReadOnlyProperty] that reflects and observes the latest emitted value.
 */
fun <T : R, R> StateFlow<T>.collectAsStateObserving(
    coroutineScope: CoroutineScope
): ReadOnlyProperty<StateChangeObserver, R> {
    return collectAsStateObserving(
        initial = value,
        coroutineScope = coroutineScope,
        policy = referentialEqualityPolicy()
    )
}

/**
 * Provides a read-write Compose-style property delegate for [MutableStateFlow] that:
 * - Observes and logs the value as Compose state via [collectAsStateObserving]
 * - Delegates mutation directly to [MutableStateFlow.value]
 *
 * Intended for use in [StateChangeObserver]-compliant classes (e.g., ViewModels).
 *
 * Example:
 * ```
 * var userPreferences by prefsFlow.collectAsMutableStateObserving(viewModelScope)
 * ```
 *
 * @param coroutineScope Coroutine scope used to collect and observe the flow.
 * @return A [ReadWriteProperty] for use as a delegated `var` property.
 */
fun <T> MutableStateFlow<T>.collectAsMutableStateObserving(
    coroutineScope: CoroutineScope
): ReadWriteProperty<StateChangeObserver, T> {
    val collected = collectAsStateObserving(coroutineScope)

    return object : ReadWriteProperty<StateChangeObserver, T> {
        override fun getValue(thisRef: StateChangeObserver, property: KProperty<*>): T {
            return collected.getValue(thisRef, property)
        }

        override fun setValue(thisRef: StateChangeObserver, property: KProperty<*>, value: T) {
            this@collectAsMutableStateObserving.value = value
        }
    }
}

/**
 * Collects a [Flow] as an observing [State] delegate.
 *
 * The emitted values are stored in a Compose [MutableState] and then wrapped in a
 * [derivedStateObservingOf] delegate to enable change observation via [StateChangeObserverRegistry].
 *
 * This allows [StateChangeObserver] implementations to respond to flow-driven state transitions.
 *
 * ### Example:
 * ```kotlin
 * val isLoading by someFlow.collectAsStateObserving(false, viewModelScope)
 * ```
 *
 * @param initial Initial value before the first flow emission.
 * @param coroutineScope Coroutine context to collect the flow.
 * @param policy Policy used to compare flow emissions for deduplication.
 */
fun <T : R, R> Flow<T>.collectAsStateObserving(
    initial: T,
    coroutineScope: CoroutineScope,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy()
): ReadOnlyProperty<StateChangeObserver, R> {
    val mutableState = mutableStateOf(
        policy = policy,
        value = initial
    )
    coroutineScope.launch {
        collect {
            mutableState.value = it
        }
    }
    return derivedStateObservingOf(
        policy = neverEqualPolicy() // Comparison already done in mutableStateOf policy
    ) {
        mutableState.value
    }
}

/**
 * A Compose-aware property delegate that observes changes in a derived [State].
 *
 * This internal class powers [derivedStateObservingOf] and integrates with
 * [StateChangeObserverRegistry] to log both the initial value and subsequent updates.
 *
 * Change detection is governed by the provided [SnapshotMutationPolicy].
 *
 * @see derivedStateObservingOf
 */
private class DerivedStateObserving<T>(
    private val policy: SnapshotMutationPolicy<T>,
    private val producer: () -> T
) : ReadOnlyProperty<StateChangeObserver, T> {

    private lateinit var state: State<T>

    override fun getValue(thisRef: StateChangeObserver, property: KProperty<*>): T {
        if (!::state.isInitialized) {
            StateChangeObserverRegistry.register(thisRef)
            val propertyName = property.name

            var lastValue: Any? = StateChangeObserverRegistry.UNINITIALIZED

            state = derivedStateOf(policy = policy) {
                val result = producer()
                @Suppress("UNCHECKED_CAST")
                if (lastValue === StateChangeObserverRegistry.UNINITIALIZED ||
                    !policy.equivalent(result, lastValue as T)
                ) {
                    StateChangeObserverRegistry.logUpdatedState(
                        observer = thisRef,
                        propertyName = propertyName,
                        value = result
                    )
                    lastValue = result
                }
                result
            }
        }
        return state.value
    }
}
