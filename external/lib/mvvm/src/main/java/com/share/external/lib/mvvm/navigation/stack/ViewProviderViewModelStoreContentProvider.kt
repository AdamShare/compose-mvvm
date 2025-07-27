package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.SaveableStateHolder
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.core.View
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.content.ViewPresentation
import com.share.external.lib.mvvm.navigation.lifecycle.DefaultViewModelStoreOwner
import com.share.external.lib.mvvm.navigation.lifecycle.LocalOwnersProvider
import com.share.external.lib.core.ViewProvider
import com.share.external.lib.core.VisibilityScopedView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.UUID

/**
 * Default implementation of [ScopedViewProvider] that wires a [view] to its [ViewModelStoreOwner],
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
open class VisibilityScopedViewProvider<V: ViewProvider>(
    protected val viewProvider: V,
    private val scope: ManagedCoroutineScope,
) : ManagedCoroutineScope by scope, ScopedViewProvider {
    override val id: UUID = UUID.randomUUID()
    override val name: String =  viewProvider.javaClass.simpleName

    private val owner = DefaultViewModelStoreOwner()

    private val visibilityScopedView = VisibilityScopedView(
        scopeFactory = { scope.create(name = name + "Visibility", context = Dispatchers.Main.immediate)  },
        viewProvider = viewProvider
    )

    override val content: @Composable (SaveableStateHolder) -> Unit = {
        owner.LocalOwnersProvider(saveableStateHolder = it, content = visibilityScopedView.content)
    }

    override fun cancel(awaitChildrenComplete: Boolean, message: String) {
        owner.clear()
        scope.cancel(awaitChildrenComplete = awaitChildrenComplete, message = message)
    }
}

interface ViewPresentationScopedViewProvider : ScopedViewProvider, ViewPresentation

@Immutable
open class NavigationVisibilityScopedViewProvider<V>(
    navigationKey: NavigationKey,
    viewProvider: V,
    scope: ManagedCoroutineScope,
) : VisibilityScopedViewProvider<V>(
    viewProvider = viewProvider,
    scope = scope,
), ViewPresentationScopedViewProvider, NavigationKey, ViewPresentation by viewProvider
        where V: ViewProvider, V: ViewPresentation {
    override val name: String = navigationKey.name
        }

