package com.share.external.lib.mvvm.navigation.content

import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope

@Stable
fun interface ViewProvider {
    /**
     * Should be called once for the duration of a view's appearance (not Android's [Activity]/[View] context).
     * Scope will cancel when the view is out of scope and a new scope is provided on each call.
     * Similar conceptually to [collectAsStateWithLifecycle].
     */
    fun create(scope: CoroutineScope): View
}