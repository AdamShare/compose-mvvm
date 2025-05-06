package com.css.android.compose.runtime

import java.util.concurrent.ConcurrentHashMap

/**
 * Internal registry for coordinating [StateChangeObserver] instances and tracking property state.
 *
 * This maintains per-observer property maps and ensures lazy, thread-safe state snapshotting.
 */
internal object StateChangeObserverRegistry {

    private val registry = ConcurrentHashMap<StateChangeObserver, ObserverState>()

    /**
     * Sentinel used to represent an uninitialized property within the registry.
     * Useful for detecting first-time mutations.
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
        if (registry.containsKey(observer)) return
        registry[observer] = ObserverState(observer = observer)
        observer.addCloseable { registry.remove(observer) }
    }

    /**
     * Records and dispatches a property value update for the given observer.
     *
     * This will track the value in the registry and invoke either [StateChangeObserver.onInitialValue]
     * or [StateChangeObserver.onValueChanged] depending on whether this property was previously observed.
     *
     * @param observer The registered observer instance.
     * @param instanceId The unique ID for the observing instance (typically class@hash).
     * @param propertyName The name of the property being updated.
     * @param value The new value to record.
     */
    fun logUpdatedState(
        observer: StateChangeObserver,
        propertyName: String,
        value: Any?
    ) {
        val state = registry.getOrPut(observer) { ObserverState(observer = observer) }
        val properties: Map<String, Any?>
        val isUpdate: Boolean

        synchronized(state.properties) {
            isUpdate = propertyName in state.properties
            state.properties[propertyName] = value
            properties = state.properties.toMap()
        }

        if (isUpdate) {
            observer.onValueChanged(
                instanceId = state.observerId,
                propertyName = propertyName,
                value = value,
                state = properties
            )
        } else {
            observer.onInitialValue(
                instanceId = state.observerId,
                propertyName = propertyName,
                value = value,
                state = properties
            )
        }
    }

    private data class ObserverState(
        val observerId: String,
        val properties: MutableMap<String, Any?>
    ) {
        constructor(observer: StateChangeObserver) : this(
            observerId = observer.instanceId(),
            properties = mutableMapOf()
        )
    }
}

/**
 * Generates a string representation of an instance in the format `ClassName@HexIdentity`.
 */
fun Any.instanceId(): String = "${javaClass.simpleName}@${identityHexString()}"

/**
 * Computes the identity-based hex string (same as Object.toString()).
 */
fun Any.identityHexString(): String = Integer.toHexString(System.identityHashCode(this))
