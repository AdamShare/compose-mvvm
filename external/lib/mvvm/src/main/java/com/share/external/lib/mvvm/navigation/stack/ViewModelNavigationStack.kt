package com.share.external.lib.mvvm.navigation.stack

import android.os.Build
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.content.ViewPresentation
import com.share.external.lib.core.ViewProvider
import kotlinx.coroutines.Dispatchers
import java.util.LinkedHashMap

/**
 * Concrete, mutable navigation stack that manages [ScopedViewProvider] instances keyed by [NavigationKey].
 *
 * Backed by a [DoublyLinkedMap], this stack integrates tightly with Compose to drive screen transitions,
 * modal overlays, and scoped lifecycle management.
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
 *
 * @see ScopedViewProvider
 * @see NavigationKey
 * @see transaction
 */
@Stable
open class ViewModelNavigationStack<V>(
    private val rootScope: ManagedCoroutineScope,
    initialStack: (NavigationStack<V>) -> Unit = {},
) : NavigationBackStack where V: ViewProvider, V: ViewPresentation {
    private val providers = linkedMapOf<NavigationKey, NavigationVisibilityScopedViewProvider<V>>()

    var stack: List<ViewPresentationScopedViewProvider> by
        mutableStateOf(value = providers.values.toList(), policy = neverEqualPolicy())
        private set

    val last by derivedStateOf { stack.lastOrNull() }

    override val size: Int by derivedStateOf { stack.size }

    private var shouldUpdateState: Boolean = false
    private var transactionRefCount: Int = 0
    private val transactionFinished: MutableList<() -> Unit> = mutableListOf()

    init {
        transaction {
            initialStack(rootNavigationScope())
        }

        // Parent scope could complete off main thread.
        rootScope.invokeOnCompletion(Dispatchers.Main.immediate) {
            removeAll()
        }
    }

    fun rootNavigationScope(): NavigationStackScope<V> = NavigationStackContext(scope = rootScope, stack = this)

    internal fun push(key: NavigationKey, scope: ManagedCoroutineScope, viewProvider: V) {
        if (!rootScope.isActive || !scope.isActive) {
            logger.a { "Scope is not active pushing $key, $viewProvider onto nav stack: $this" }
            return
        }
        if (providers.keys.lastOrNull() == key) {
            return
        }
        val previous = providers[key]
        val provider = NavigationVisibilityScopedViewProvider(
            navigationKey = key,
            viewProvider = viewProvider,
            scope = scope,
        )
        providers[key] = provider

        transactionFinished.add {
            previous?.cancel(awaitChildrenComplete = true, message = "Pushed new content for key: $key")

            scope.invokeOnCompletion(Dispatchers.Main.immediate) { remove(key) }
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

private fun <K, V>LinkedHashMap<K, V>.removeAllAfter(key: K, inclusive: Boolean = false): List<V> {
    if (!containsKey(key)) {
        return listOf()
    }
    val removed = mutableListOf<V>()

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        val reversedEntrySet = sequencedEntrySet().reversed().iterator()
        var element = reversedEntrySet.next()
        while (element.key != key) {
            removed.add(element.value)
            reversedEntrySet.remove()
            element = reversedEntrySet.next()
        }
        if (inclusive) {
            removed.add(element.value)
            reversedEntrySet.remove()
        }
        removed
    } else {
        val iterator = entries.iterator()

        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key == key) {
                if (inclusive) {
                    removed.add(entry.value)
                    iterator.remove()
                }
                break
            }
        }

        while (iterator.hasNext()) {
            removed.add(iterator.next().value)
            iterator.remove()
        }

        removed.asReversed()
    }
}

fun <K, V> LinkedHashMap<K, V>.removeLast(): V? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        if (isEmpty()) null else sequencedValues().removeLast()
    } else {
        keys.lastOrNull()?.let { remove(key = it) }
    }
}

fun <T: ScopedViewProvider> Logger.logEntries(
    entries: List<T>,
    name: String,
    metadata: (T) -> String? = { null },
) = d {
    buildString {
        append("Backstack $name[")
        entries.forEachIndexed { i, provider ->
            append("{${provider.name}")
            metadata(provider)?.let { append(": $it") }
            append("}")
            if (i < entries.size - 1) append(" ⇨ ")
        }
        append("]")
    }
}
