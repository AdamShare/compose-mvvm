package com.share.external.foundation.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun Job.invokeOnCompletion(context: CoroutineContext, handler: (cause: Throwable?) -> Unit): DisposableHandle {
    return invokeOnCompletion {
        CoroutineScope(context).launch {
            handler(it)
        }
    }
}