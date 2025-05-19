package com.share.external.foundation.coroutines

interface CloseableRegistry {
    /**
     * Registers a cleanup hook to be invoked when the scope ends.
     *
     * @param closeable A handle that will be invoked exactly once on lifecycle disposal.
     */
    fun addCloseable(closeable: AutoCloseable)
}
