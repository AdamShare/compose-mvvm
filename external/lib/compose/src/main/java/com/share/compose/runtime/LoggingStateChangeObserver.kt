package com.share.compose.runtime

import com.share.external.foundation.coroutines.CloseableRegistry
import timber.log.Timber

/**
 * Default implementation of [StateChangeObserver] that logs state changes using Timber.
 *
 * Logs include the instance identifier, property name, and full state snapshot on update.
 * This can be used for debugging or development-time introspection.
 */
interface LoggingStateChangeObserver : StateChangeObserver, CloseableRegistry {

    override fun onInitialValue(instanceId: String, propertyName: String, value: Any?, state: Map<String, Any?>) {
        Timber.tag(TAG).v("%s(initialized: %s=%s)", instanceId, propertyName, value)
    }

    override fun onValueChanged(instanceId: String, propertyName: String, value: Any?, state: Map<String, Any?>) {
        Timber.tag(TAG).v("%s(updated: %s, state: %s)", instanceId, propertyName, state)
    }

    companion object {
        /** The default Timber tag used for logging state changes. */
        const val TAG: String = "StateChangeObserver"
    }
}
