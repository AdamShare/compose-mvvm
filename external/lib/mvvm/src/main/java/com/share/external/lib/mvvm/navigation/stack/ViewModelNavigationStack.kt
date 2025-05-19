package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import com.share.external.foundation.collections.DoublyLinkedMap
import com.share.external.foundation.collections.doublyLinkedMapOf
import com.share.external.foundation.collections.removeLast
import com.share.external.foundation.coroutines.MainImmediateScope
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.lifecycle.ViewLifecycleScope
import com.share.external.lib.mvvm.navigation.lifecycle.ViewLifecycleScopeImpl
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Concrete, mutable navigation stack. All public methods forward to
 * modifications on an internal [DoublyLinkedMap] and trigger Compose
 * recomposition via a snapshot state.
 */
open class ViewModelNavigationStack<V>(
    private val rootScope: ManagedCoroutineScope,
) : NavigationBackStack {
    private val providers = doublyLinkedMapOf<NavigationKey, ViewModelStoreContentProvider<V>>()

    var stack: DoublyLinkedMap<NavigationKey, ViewModelStoreContentProvider<V>> by mutableStateOf(
        value = providers,
        policy = neverEqualPolicy()
    )
        private set

    fun rootContext(): NavigationStackScope<V> = NavigationStackContext(
        scope = rootScope,
        stack = this
    )

    override val size: Int by derivedStateOf {
        stack.size
    }

    protected val last by derivedStateOf {
        stack.values.lastOrNull()
    }

    init {
        rootScope.invokeOnCompletion {
            // Parent scope could complete off main thread.
            MainImmediateScope().launch {
                removeAll()
            }
        }
    }

    internal fun push(key: NavigationKey, content: V, scope: ViewLifecycleScopeImpl) {
        if (!rootScope.isActive || !scope.isActive) {
            Timber.tag(TAG).wtf(
                "Scope is not active pushing $key, $content onto nav stack: $this"
            )
            return
        }
        if (providers.keys.lastOrNull() == key) {
            return
        }
        val previous = providers[key]
        providers[key] = ViewModelStoreContentProviderImpl(
            view = content,
            scope = scope
        )
        updateState()
        previous?.cancel(
            awaitChildrenComplete = true,
            message = "Pushed new content for key: $key"
        )

        scope.invokeOnCompletion {
            MainImmediateScope().launch {
                remove(key)
            }
        }
    }

    override fun pop(): Boolean {
        return providers.removeLast()?.run {
            updateState()
            cancel(
                awaitChildrenComplete = true,
                message = "Popped from back stack",
            )
        } != null
    }

    override fun popTo(key: NavigationKey, inclusive: Boolean): Boolean {
        val removed = providers.removeAllAfter(key, inclusive)
        return if (removed.isNotEmpty()) {
            updateState()
            removed.asReversed().forEach {
                it.cancel(
                    awaitChildrenComplete = true,
                    message = "Popped from back stack to: $key inclusive: $inclusive"
                )
            }
            true
        } else false
    }

    override fun removeAll() {
        providers.keys.firstOrNull()?.let {
            popTo(
                key = it,
                inclusive = true
            )
        }
    }

    override fun remove(key: NavigationKey) {
        providers.remove(key)?.run {
            updateState()
            cancel(
                awaitChildrenComplete = true,
                message = "Removed from back stack"
            )
        }
    }

    private fun updateState() {
        stack = providers
    }

    companion object {
        const val TAG = "ViewModelNavigationStack"
    }
}
