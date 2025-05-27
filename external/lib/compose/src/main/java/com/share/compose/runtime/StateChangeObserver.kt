package com.share.compose.runtime

/**
 * Observes Compose snapshot state changes within a ViewModel or other lifecycle-aware owner.
 *
 * Implement this interface to monitor and react to [MutableState] and [derivedStateOf] changes. Typical use cases
 * include:
 * - Logging or debugging property-level state transitions
 * - Sending analytics for specific field changes
 * - Capturing state snapshots for QA or performance analysis
 *
 * This observer is intended to be used in conjunction with [StateChangeObserverRegistry], and is automatically notified
 * when properties delegate to helpers like [mutableStateObservingOf] or [derivedStateObservingOf].
 */
interface StateChangeObserver : com.share.external.foundation.coroutines.CloseableRegistry {

    /**
     * Called the first time a property's value is initialized and observed.
     *
     * This occurs when the backing Compose state is created and the property is read or written.
     *
     * @param instanceId A unique identifier for the owning instance (e.g. "MyViewModel@abc123").
     * @param propertyName The name of the state-bound property.
     * @param value The value assigned during initialization.
     * @param state A snapshot of all current property values observed for this instance.
     */
    fun onInitialValue(instanceId: String, propertyName: String, value: Any?, state: Map<String, Any?>)

    /**
     * Called whenever a property's value changes after initialization.
     *
     * Only called when the change passes the defined mutation policy (i.e. the new value is not equivalent).
     *
     * @param instanceId A unique identifier for the owning instance (e.g. "MyViewModel@abc123").
     * @param propertyName The name of the changed property.
     * @param value The new value assigned.
     * @param state A snapshot of all current property values observed for this instance.
     */
    fun onValueChanged(instanceId: String, propertyName: String, value: Any?, state: Map<String, Any?>)
}
