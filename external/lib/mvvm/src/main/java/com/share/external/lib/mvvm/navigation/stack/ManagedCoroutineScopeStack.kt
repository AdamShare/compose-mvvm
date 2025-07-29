package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.share.external.foundation.coroutines.ManagedCancellable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import kotlinx.coroutines.Dispatchers

/**
 * Concrete, mutable navigation stack that manages [ScopedViewProvider] instances keyed by [NavigationKey].
 *
 * ### Features:
 * - State is exposed via [stack], a [mutableStateOf] snapshot-backed map to trigger Compose recomposition.
 * - Supports nested transaction batching via [transactionRefCount] to defer recomposition and lifecycle effects.
 * - Pushes views onto the stack with [push], removing and cancelling existing entries with matching keys.
 * - Provides [pop], [popTo], and [removeAll] operations for typical back stack navigation.
 * - Defers cancellation logic (e.g., ViewModel teardown) until the transaction is complete using [transactionFinished].
 *
 * @param V The view type managed by the stack.
 * @param rootScope The parent coroutine scope for all views in the stack.
 * @param initialStack Optional lambda to prepopulate the stack in a single transaction.
 * @see ScopedViewProvider
 * @see NavigationKey
 * @see transaction
 */
@Stable
open class ManagedCoroutineScopeStack<V, E: ManagedCancellable>(
    private val rootScope: ManagedCoroutineScope,
    private val entryFactory: (key: NavigationKey, scope: ManagedCoroutineScope, viewProvider: V) -> E,
    initialStack: (NavigationStack<V>) -> Unit = {},
) : NavigationBackStack {
    private val providers = linkedMapOf<NavigationKey, E>()

    var stack: List<E> by mutableStateOf(value = listOf(), policy = neverEqualPolicy())
        private set

    override val size: Int by derivedStateOf { stack.size }

    private var shouldUpdateState: Boolean = false
    private var transactionRefCount: Int = 0
    private val transactionFinished: MutableList<() -> Unit> = mutableListOf()

    init {
        transaction { initialStack(rootNavigationScope()) }

        // Parent scope could complete off main thread.
        rootScope.invokeOnCompletion(context = Dispatchers.Main.immediate) { removeAll() }
    }

    fun rootNavigationScope(): NavigationStackScope<V> = NavigationStackScopeImpl(scope = rootScope, stack = this)

    fun push(key: NavigationKey, scope: ManagedCoroutineScope, viewProvider: V) {
        if (!rootScope.isActive || !scope.isActive) {
            logger.a { "Scope is not active pushing $key, $viewProvider onto nav stack: $this" }
            return
        }
        if (providers.keys.lastOrNull() == key) {
            return
        }
        val previous = providers[key]
        val provider = entryFactory(key, scope, viewProvider)
        providers[key] = provider

        transactionFinished.add {
            previous
                ?.cancel(awaitChildrenComplete = true, message = "Pushed new content for key: $key")

            scope.invokeOnCompletion(context = Dispatchers.Main.immediate) { remove(key) }
        }

        updateState()
    }

    override fun pop(): Boolean {
        return providers.removeLast()?.run {
            transactionFinished.add {
                cancel(awaitChildrenComplete = true, message = "Popped from back stack")
            }
            updateState()
        } != null
    }

    override fun popTo(key: NavigationKey, inclusive: Boolean): Boolean {
        val removed = providers.removeAllAfter(key, inclusive)
        return if (removed.isNotEmpty()) {
            transactionFinished.add {
                removed.forEach {
                    it.cancel(
                        awaitChildrenComplete = true,
                        message = "Popped from back stack to: $key inclusive: $inclusive",
                    )
                }
            }
            updateState()
            true
        } else false
    }

    override fun removeAll() {
        providers.keys.firstOrNull()?.let { popTo(key = it, inclusive = true) }
    }

    override fun remove(key: NavigationKey) {
        providers.remove(key)?.run {
            transactionFinished.add {
                cancel(awaitChildrenComplete = true, message = "Removed from back stack")
            }
            updateState()
        }
    }

    final override fun transaction(block: () -> Unit) {
        transactionRefCount += 1
        try {
            block()
        } finally {
            transactionRefCount -= 1
            if (shouldUpdateState && transactionRefCount == 0) {
                shouldUpdateState = false
                updateState()
            }
        }
    }

    private fun updateState() {
        if (transactionRefCount > 0) {
            shouldUpdateState = true
        } else {
            stack = providers.values.toList()
            transactionFinished.forEach { it() }
            transactionFinished.clear()
        }
    }

    companion object {
        private val logger = Logger.withTag("NavigationStack")
    }
}

