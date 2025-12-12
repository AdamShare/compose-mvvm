package com.share.external.lib.navigation.multidisplay

import androidx.compose.runtime.Stable
import com.share.external.foundation.coroutines.ManagedCancellable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.navigation.stack.ManagedCoroutineScopeStack
import com.share.external.lib.navigation.stack.NavigationStack
import com.share.external.lib.view.ViewKey
import com.share.external.lib.view.ViewPresentation
import com.share.external.lib.view.ViewProvider
import com.share.external.lib.view.ViewScopeProvider

/**
 * A navigation stack that supports rendering views across multiple displays.
 *
 * This stack extends [ManagedCoroutineScopeStack] to handle scenarios where the same navigation
 * entry may need to render different views depending on the active display (e.g., primary screen
 * vs. secondary screen, or phone vs. tablet pane).
 *
 * ### Multi-Display Support
 * Each entry in the stack provides a [MultiDisplayViewProvider] that can return different views
 * for different [Display] configurations. This enables:
 * - Showing a detail view on a secondary display while keeping the list on primary
 * - Adaptive layouts where content shifts between panes based on screen configuration
 *
 * @param V The view type, which must implement both [ViewProvider] and [ViewPresentation].
 * @param D The display type that identifies different rendering targets.
 * @param rootScope The parent scope that owns this navigation stack's lifecycle.
 * @param viewScopeProviderFactory Factory for creating [ViewScopeProvider] instances.
 * @param initialStack Optional lambda to prepopulate the stack during construction.
 */
class MultiDisplayNavigationStack<V, D: Display>(
    rootScope: ManagedCoroutineScope,
    viewScopeProviderFactory: ViewScopeProvider.Factory = ViewScopeProvider.Factory.Default,
    initialStack: (NavigationStack<MultiDisplayViewProvider<V, D>>) -> Unit = {},
): ManagedCoroutineScopeStack<MultiDisplayViewProvider<V, D>, MultiViewScopeProvider<D>>(
    rootScope = rootScope,
    entryFactory = { key, scope, viewProvider ->
        MultiViewProviderImpl(
            navigationKey = key,
            scope = scope,
            viewProvider = viewProvider,
            viewScopeProviderFactory = viewScopeProviderFactory,
        )
    },
    initialStack = initialStack,
) where V: ViewProvider, V: ViewPresentation

/**
 * Provides views for different display configurations.
 *
 * Implement this interface to define which view should be shown on each display type.
 * Return `null` for displays where this entry should not render content.
 *
 * @param V The view type.
 * @param D The display type.
 */
@Stable
interface MultiDisplayViewProvider<V, D: Display> {
    /**
     * Returns the view for the specified display, or `null` if this entry has no content
     * for that display.
     */
    fun get(display: D): V?
}

/**
 * Identifies a display target for multi-display navigation.
 *
 * Implement this interface to define display types in your application
 * (e.g., primary/secondary screens, phone/tablet panes).
 */
interface Display {
    /**
     * Whether this is the default (primary) display.
     */
    val isDefault: Boolean
}

/**
 * Manages view scope providers for multiple displays.
 *
 * This interface provides access to the [ViewProvider] and [ViewScopeProvider] for each
 * display configuration, allowing the navigation host to render content appropriately.
 *
 * @param D The display type.
 */
interface MultiViewScopeProvider<D: Display>: ManagedCancellable {
    /**
     * Returns the [ViewProvider] for the specified display, or `null` if no content exists.
     */
    fun viewProvider(display: D): ViewProvider?

    /**
     * Returns the [ViewScopeProvider] for the specified display, creating it if needed.
     */
    fun scopedViewProvider(display: D): ViewScopeProvider?
}

internal class MultiViewProviderImpl<V: ViewProvider, D: Display>(
    val navigationKey: ViewKey,
    val scope: ManagedCoroutineScope,
    val viewProvider: MultiDisplayViewProvider<V, D>,
    val viewScopeProviderFactory: ViewScopeProvider.Factory,
): MultiViewScopeProvider<D> {
    private val scopedViewProviders = mutableMapOf<D, ViewScopeProvider?>()

    override fun viewProvider(display: D): V? = viewProvider.get(display)

    override fun scopedViewProvider(display: D) = scopedViewProviders.getOrPut(display) {
        viewProvider(display)?.let {
            viewScopeProviderFactory(
                name = navigationKey.name,
                onViewAppear = it::onViewAppear,
                scope = scope
            )
        }
    }

    override fun cancel(awaitChildrenComplete: Boolean, message: String) {
        scopedViewProviders.values.forEach { it?.cancel(awaitChildrenComplete, message) }
    }
}
