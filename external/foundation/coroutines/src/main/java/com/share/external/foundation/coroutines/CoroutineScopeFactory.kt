package com.share.external.foundation.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope

/**
 * A factory for creating new [CoroutineScope] instances with a specific name and optional context. This is typically
 * used to produce child scopes within a parent scope.
 *
 * Implementations might choose different strategies for job creation
 * (e.g. using a [kotlinx.coroutines.SupervisorJob] vs a standard [kotlinx.coroutines.Job]) or context merging.
 */
fun interface CoroutineScopeFactory {
    /**
     * Creates a new [CoroutineScope] with a given [name] and [context].
     *
     * @param name A human-readable label for debugging or logging.
     * @param context Additional [CoroutineContext] elements.
     * @return A new [CoroutineScope] instance.
     */
    fun create(name: String, context: CoroutineContext): CoroutineScope

    /**
     * Creates a new [CoroutineScope] with a given [name] and [EmptyCoroutineContext].
     *
     * @param name A human-readable label for debugging or logging.
     * @return A new [CoroutineScope] instance.
     */
    fun create(name: String): CoroutineScope = create(name, EmptyCoroutineContext)
}
