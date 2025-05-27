package com.share.external.foundation.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job

interface CoroutineScopeProvider : CloseableRegistry {
    val scope: CoroutineScope

    override fun addCloseable(closeable: AutoCloseable) {
        scope.coroutineContext.job.invokeOnCompletion { closeable.close() }
    }
}
