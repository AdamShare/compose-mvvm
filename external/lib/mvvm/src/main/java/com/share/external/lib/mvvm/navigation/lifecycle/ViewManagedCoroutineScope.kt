package com.share.external.lib.mvvm.navigation.lifecycle

import androidx.compose.runtime.Stable
import com.share.external.foundation.coroutines.CoroutineScopeFactory
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.View

@Stable
class ViewManagedCoroutineScope(
    private val actual: ManagedCoroutineScope,
): CoroutineScopeFactory by actual {
    internal fun cancel(message: String) {
        actual.cancel(message = message)
    }
}

@Stable
fun interface ViewProvider {
    /**
     * Should be called once for the duration of a view's appearance (not Android's [Activity]/[View] context).
     * Scope will cancel when the view is out of scope and a new scope is provided on each call.
     * Similar conceptually to [collectAsStateWithLifecycle].
     */
    fun create(scope: ViewManagedCoroutineScope): View
}