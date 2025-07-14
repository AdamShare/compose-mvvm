package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.saveable.SaveableStateHolder
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.content.ViewPresentation
import com.share.external.lib.mvvm.navigation.lifecycle.DefaultViewModelStoreOwner
import com.share.external.lib.mvvm.navigation.lifecycle.LocalOwnersProvider
import com.share.external.lib.mvvm.base.ViewProvider
import com.share.external.lib.mvvm.navigation.lifecycle.VisibilityScopedView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Default implementation of [ViewModelStoreContentProvider] that wires a [view] to its [ViewModelStoreOwner],
 * [CoroutineScope], and lifecycle visibility events.
 *
 * This class ensures that:
 * - The [view] has a retained [ViewModelStore] scoped to its lifecycle.
 * - Coroutine scope is tied to the view's appearance and cancelled appropriately.
 *
 * @param view The view instance to be hosted.
 * @param scope The view-scoped lifecycle and coroutine scope.
 */
@Immutable
internal open class ViewProviderViewModelStoreContentProvider<V>(
    protected val viewProvider: V,
    private val scope: ManagedCoroutineScope,
) : ManagedCoroutineScope by scope,
    ViewModelStoreContentProvider<VisibilityScopedView<V>>
        where V: ViewProvider {
            private val tag = viewProvider.javaClass.simpleName

    private val owner = DefaultViewModelStoreOwner()

    override val view: VisibilityScopedView<V> = VisibilityScopedView(
        scopeFactory = { scope.create(name = tag, context = Dispatchers.Main.immediate)  },
        viewProvider = viewProvider
    )

    override fun cancel(awaitChildrenComplete: Boolean, message: String) {
        owner.clear()
        scope.cancel(awaitChildrenComplete = awaitChildrenComplete, message = message)
    }

    @Composable
    override fun LocalOwnersProvider(saveableStateHolder: SaveableStateHolder, content: @Composable () -> Unit) {
        owner.LocalOwnersProvider(saveableStateHolder, content)
    }
}

@Immutable
internal open class ViewPresentationProviderViewModelStoreContentProvider<V>(
    navigationKey: NavigationKey,
    viewProvider: V,
    scope: ManagedCoroutineScope,
) : ViewProviderViewModelStoreContentProvider<V>(
    viewProvider = viewProvider,
    scope = scope,
), NavigationKey by navigationKey, ViewPresentation where V: ViewProvider, V: ViewPresentation {
    override val preferredPresentationStyle = viewProvider.preferredPresentationStyle
}