package com.share.external.lib.navigation.stack

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.share.external.foundation.coroutines.ManagedCancellable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.ViewKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.decrementAndFetch
import kotlin.concurrent.atomics.incrementAndFetch

/**
 * Concrete, mutable navigation stack that manages [ScopedViewProvider] instances keyed by [ViewKey].
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
 * @see ViewKey
 * @see transaction
 */
@Stable
open class ManagedCoroutineScopeStack<V, E: ManagedCancellable> internal constructor(
    initialStack: suspend (NavigationStack<V>) -> Unit,
    private val entryFactory: (key: ViewKey, scope: ManagedCoroutineScope, viewProvider: V) -> E,
    private val rootScope: ManagedCoroutineScope,
    private val scope: CoroutineScope,
) : NavigationBackStack {
    constructor(
        entryFactory: (key: ViewKey, scope: ManagedCoroutineScope, viewProvider: V) -> E,
        rootScope: ManagedCoroutineScope,
        initialStack: suspend (NavigationStack<V>) -> Unit = {},
    ): this(
        entryFactory = entryFactory,
        initialStack = initialStack,
        rootScope = rootScope,
        scope = rootScope.create("ManagedCoroutineScopeStackInit", context = Dispatchers.Main.immediate),
    )

    private val providers = linkedMapOf<ViewKey, E>()

    var stack: List<E> by mutableStateOf(value = listOf(), policy = neverEqualPolicy())
        private set

    override val size: Int by derivedStateOf { stack.size }

    private var shouldUpdateState: Boolean = false
    private var transactionRefCount: Int = 0
    private val transactionFinished: MutableList<() -> Unit> = mutableListOf()

    init {
        scope.launch {
            transaction { initialStack(rootNavigationScope()) }
        }

        // Parent scope could complete off main thread.
        rootScope.invokeOnCompletion(context = Dispatchers.Main.immediate) { removeAll() }
    }

    fun rootNavigationScope(): NavigationStackScope<V> = NavigationStackScopeImpl(scope = rootScope, stack = this)

    fun push(key: ViewKey, scope: ManagedCoroutineScope, viewProvider: V) {
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

    override fun popTo(key: ViewKey, inclusive: Boolean): Boolean {
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

    override fun remove(key: ViewKey) {
        providers.remove(key)?.run {
            transactionFinished.add {
                cancel(awaitChildrenComplete = true, message = "Removed from back stack")
            }
            updateState()
        }
    }

    final override suspend fun transaction(block: suspend () -> Unit) = scope.async {
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
    }.await()

    @OptIn(ExperimentalAtomicApi::class)
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
