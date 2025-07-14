package com.share.external.lib.compose.runtime

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.structuralEqualityPolicy
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Creates a [MutableState] delegate that reports value changes through [StateChangeObserverRegistry].
 *
 * This delegate should be used within a class that implements [StateChangeObserver]. The backing state uses the
 * specified [SnapshotMutationPolicy] to determine whether a change should be logged.
 *
 * @param value The initial value for the state.
 * @param policy The mutation policy that governs change detection (defaults to [structuralEqualityPolicy]).
 */
fun <T> mutableStateObservingOf(
    value: T,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
): ReadWriteProperty<StateChangeObserver, T> =
    MutableStateObserving(
        equivalent = policy::equivalent,
        stateFactory = { mutableStateOf(policy = policy, value = value) },
    )

/** Creates a [MutableState] delegate for [Int] that reports changes through [StateChangeObserverRegistry]. */
fun mutableIntStateObservingOf(value: Int): ReadWriteProperty<StateChangeObserver, Int> =
    MutableStateObserving(equivalent = { a, b -> a == b }, stateFactory = { mutableIntStateOf(value) })

/** Creates a [MutableState] delegate for [Long] that reports changes through [StateChangeObserverRegistry]. */
fun mutableLongStateObservingOf(value: Long): ReadWriteProperty<StateChangeObserver, Long> =
    MutableStateObserving(equivalent = { a, b -> a == b }, stateFactory = { mutableLongStateOf(value) })

/** Creates a [MutableState] delegate for [Double] that reports changes through [StateChangeObserverRegistry]. */
fun mutableDoubleStateObservingOf(value: Double): ReadWriteProperty<StateChangeObserver, Double> =
    MutableStateObserving(equivalent = { a, b -> a == b }, stateFactory = { mutableDoubleStateOf(value) })

/** Creates a [MutableState] delegate for [Float] that reports changes through [StateChangeObserverRegistry]. */
fun mutableFloatStateObservingOf(value: Float): ReadWriteProperty<StateChangeObserver, Float> =
    MutableStateObserving(equivalent = { a, b -> a == b }, stateFactory = { mutableFloatStateOf(value) })

/**
 * A property delegate that wraps a Compose [MutableState] and reports all changes to [StateChangeObserverRegistry].
 *
 * Designed for use in classes that implement [StateChangeObserver], such as ViewModels. Automatically logs:
 * - The initial value upon first access (via [getValue] or [setValue])
 * - Each subsequent change, if it differs from the previous value based on the [equivalent] comparison
 *
 * ### Example Usage
 *
 * ```kotlin
 * class MyViewModel : ViewModel(), StateChangeObserver {
 *     var totalCount by mutableIntStateObservingOf(0)
 * }
 * ```
 *
 * @param equivalent A function used to compare old and new values for equality.
 * @param stateFactory A lambda that produces the underlying [MutableState] instance.
 */
class MutableStateObserving<T>(private val equivalent: (T, T) -> Boolean, stateFactory: () -> MutableState<T>) :
    ReadWriteProperty<StateChangeObserver, T> {

    private val state: MutableState<T> = stateFactory()
    private lateinit var onValueChanged: (T) -> Unit

    override fun getValue(thisRef: StateChangeObserver, property: KProperty<*>): T {
        initializeIfNeeded(thisRef, property)
        return state.value
    }

    override fun setValue(thisRef: StateChangeObserver, property: KProperty<*>, value: T) {
        initializeIfNeeded(thisRef, property)
        if (!equivalent(value, state.value)) {
            onValueChanged(value)
        }
        state.value = value
    }

    /**
     * Lazily initializes logging behavior when the delegate is first accessed.
     *
     * If the owning instance implements [StateChangeObserver], it will be registered and tracked. If not, logging is
     * disabled and a warning is emitted.
     */
    private fun initializeIfNeeded(thisRef: StateChangeObserver, property: KProperty<*>) {
        if (::onValueChanged.isInitialized) return

        StateChangeObserverRegistry.register(thisRef)
        val propertyName = property.name
        onValueChanged = { newValue ->
            StateChangeObserverRegistry.logUpdatedState(
                observer = thisRef,
                propertyName = propertyName,
                value = newValue,
            )
        }

        // Immediately log the initial value
        onValueChanged(state.value)
    }

    companion object {
        const val TAG = "MutableStateObserving"
    }
}
