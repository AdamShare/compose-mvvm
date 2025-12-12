package com.share.external.foundation.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job

/**
 * A provider that exposes a [CoroutineScope] and automatically manages cleanup of registered closeables.
 *
 * This interface combines scope access with lifecycle-aware resource management. Any [AutoCloseable]
 * registered via [addCloseable] will be automatically closed when the scope's job completes.
 *
 * ### Usage
 * Implement this interface when you need to expose a coroutine scope and want resources to be
 * automatically cleaned up when the scope ends:
 *
 * ```kotlin
 * class MyViewModel : CoroutineScopeProvider {
 *     override val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
 *
 *     init {
 *         val connection = database.openConnection()
 *         addCloseable(connection) // Will be closed when scope completes
 *     }
 * }
 * ```
 */
interface CoroutineScopeProvider : CloseableRegistry {
    /**
     * The [CoroutineScope] managed by this provider.
     */
    val scope: CoroutineScope

    override fun addCloseable(closeable: AutoCloseable) {
        scope.coroutineContext.job.invokeOnCompletion { closeable.close() }
    }
}
