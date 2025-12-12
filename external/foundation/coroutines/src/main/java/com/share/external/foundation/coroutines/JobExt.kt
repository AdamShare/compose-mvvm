package com.share.external.foundation.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Registers a completion handler that executes within the specified [CoroutineContext].
 *
 * Unlike the standard [Job.invokeOnCompletion], which runs the handler synchronously on an
 * arbitrary thread, this extension launches the handler as a coroutine in the provided context.
 * This is useful when the handler needs to run on a specific dispatcher (e.g., Main) or needs
 * access to context elements like a [kotlinx.coroutines.CoroutineName].
 *
 * @param context The [CoroutineContext] in which to execute the handler.
 * @param handler The callback invoked when this job completes, receiving the completion cause
 *   (null if completed normally, or a [Throwable] if cancelled/failed).
 * @return A [DisposableHandle] that can be used to unregister the handler before completion.
 */
fun Job.invokeOnCompletion(context: CoroutineContext, handler: (cause: Throwable?) -> Unit): DisposableHandle {
    return invokeOnCompletion {
        CoroutineScope(context).launch {
            handler(it)
        }
    }
}