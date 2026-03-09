package com.getbackcompose.compose.runtime

import co.touchlab.kermit.Logger
import com.getbackcompose.foundation.coroutines.CloseableRegistry

/**
 * Default implementation of [StateChangeObserver] that logs state changes using Timber.
 *
 * Logs include the instance identifier, property name, and full state snapshot on update. This can be used for
 * debugging or development-time introspection.
 */
interface LoggingStateChangeObserver : StateChangeObserver, CloseableRegistry {

    override fun onInitialValue(instanceId: String, propertyName: String, value: Any?, state: Map<String, Any?>) {
        logger.v { "$instanceId(initialized: $propertyName=$value)" }
    }

    override fun onValueChanged(instanceId: String, propertyName: String, value: Any?, state: Map<String, Any?>) {
        logger.v { "$instanceId(updated: $propertyName, state: $state)" }
    }

    companion object {
        /** The default Timber tag used for logging state changes. */
        private val logger = Logger.withTag("StateChangeObserver")
    }
}
