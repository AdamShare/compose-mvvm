package com.share.external.lib.core

import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope

@Stable
fun interface ViewProvider {
    /**
     * Should be called once for the duration of a view's appearance
     * (not to be confused with Android's [android.app.Activity]/[android.view.View] context lifecycle).
     *
     * Scope will cancel when the view is out of scope and a new scope is provided on each call.
     * Similar conceptually to [androidx.lifecycle.compose.collectAsStateWithLifecycle].
     */
    fun onViewAppear(scope: CoroutineScope): View
}