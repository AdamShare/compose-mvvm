package com.share.external.lib.activity.application

import com.share.external.foundation.coroutines.CoroutineScopeFactory
import com.share.external.foundation.coroutines.childSupervisorJobScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * A [CoroutineScopeFactory] tied to the Application lifecycle.
 *
 * This interface provides application-scoped coroutine scopes that outlive individual
 * Activities and can be used for long-running operations that should survive configuration
 * changes but not outlive the Application process.
 *
 * ### Implementation
 * Implement this interface on your Application class:
 * ```kotlin
 * class MyApplication : Application(), ApplicationCoroutineScopeFactory
 * ```
 *
 * All scopes created via [create] are children of the singleton application scope,
 * using [SupervisorJob] to prevent child failures from cancelling siblings.
 */
interface ApplicationCoroutineScopeFactory: CoroutineScopeFactory {
    override fun create(name: String, context: CoroutineContext): CoroutineScope {
        return applicationScope.childSupervisorJobScope(
            name = name,
            context = context
        )
    }

    companion object {
        private val applicationScope = CoroutineScope(
            context = SupervisorJob() + Dispatchers.Default + CoroutineName("Application")
        )
    }
}