package com.share.external.lib.mvvm.navigation.lifecycle

import androidx.savedstate.SavedState
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.core.View
import com.share.external.lib.mvvm.navigation.scope.ViewScopeProvider
import kotlinx.coroutines.CoroutineScope

class LifecycleViewScopeProvider private constructor(
    name: String,
    onViewAppear: (CoroutineScope) -> View,
    scope: ManagedCoroutineScope,
    private val viewModelStoreOwner: DefaultViewModelStoreOwner,
): ViewScopeProvider(
    name = name,
    onViewAppear = {
        val content = onViewAppear(it).content
        View {
            viewModelStoreOwner.LocalOwnersProvider(content = content)
        }
    },
    scope = scope,
) {
    constructor(
        name: String,
        onViewAppear: (CoroutineScope) -> View,
        savedState: SavedState?,
        scope: ManagedCoroutineScope,
    ): this(
        name = name,
        onViewAppear = onViewAppear,
        scope = scope,
        viewModelStoreOwner = DefaultViewModelStoreOwner(savedState = savedState),
    )

    override fun cancel(awaitChildrenComplete: Boolean, message: String) {
        super.cancel(awaitChildrenComplete, message)
        viewModelStoreOwner.clear()
    }
}