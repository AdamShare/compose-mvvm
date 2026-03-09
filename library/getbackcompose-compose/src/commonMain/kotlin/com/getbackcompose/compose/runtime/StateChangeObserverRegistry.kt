package com.getbackcompose.compose.runtime

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

/**
 * Internal registry for coordinating [StateChangeObserver] instances and tracking property state.
 *
 * This maintains per-observer property maps and ensures lazy, thread-safe state snapshotting.
 */
internal object StateChangeObserverRegistry : SynchronizedObject() {

    private val registry = mutableMapOf<StateChangeObserver, ObserverState>()

    /**
     * Sentinel used to represent an uninitialized property within the registry. Useful for detecting first-time
     * mutations.
     */
    object UNINITIALIZED

    /**
     * Registers a new observer and installs automatic lifecycle cleanup.
     *
     * If the observer is already registered, this call is ignored.
     *
     * @param observer The observer to register.
     */
    fun register(observer: StateChangeObserver) {
        synchronized(this) {
            if (registry.containsKey(observer)) return
            registry[observer] = ObserverState(observer = observer)
        }
        observer.addCloseable(AutoCloseable { synchronized(this) { registry.remove(observer) } })
    }

    /**
     * Records and dispatches a property value update for the given observer.
     *
     * This will track the value in the registry and invoke either [StateChangeObserver.onInitialValue] or
     * [StateChangeObserver.onValueChanged] depending on whether this property was previously observed.
     *
     * @param observer The registered observer instance.
     * @param instanceId The unique ID for the observing instance (typically class@hash).
     * @param propertyName The name of the property being updated.
     * @param value The new value to record.
     */
    fun logUpdatedState(observer: StateChangeObserver, propertyName: String, value: Any?) {
        val result = synchronized(this) {
            val state = registry.getOrPut(observer) { ObserverState(observer = observer) }
            val isUpdate = propertyName in state.properties
            state.properties[propertyName] = value
            Triple(state.observerId, isUpdate, state.properties.toMap())
        }
        val (observerId, isUpdate, properties) = result

        if (isUpdate) {
            observer.onValueChanged(
                instanceId = observerId,
                propertyName = propertyName,
                value = value,
                state = properties,
            )
        } else {
            observer.onInitialValue(
                instanceId = observerId,
                propertyName = propertyName,
                value = value,
                state = properties,
            )
        }
    }

    private data class ObserverState(val observerId: String, val properties: MutableMap<String, Any?>) {
        constructor(
            observer: StateChangeObserver
        ) : this(observerId = observer.instanceId(), properties = mutableMapOf())
    }
}

/** Generates a string representation of an instance in the format `ClassName@HexIdentity`. */
fun Any.instanceId(): String = "${this::class.simpleName}@${identityHexString()}"

/** Computes the identity-based hex string. */
fun Any.identityHexString(): String = hashCode().toUInt().toString(16)
