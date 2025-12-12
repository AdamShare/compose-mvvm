package com.share.external.lib.navigation.switcher

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.ViewKey
import com.share.external.lib.view.ViewProvider
import com.share.external.lib.view.ViewScopeProvider

/**
 * Manages switching between multiple views identified by [ViewKey].
 *
 * A ViewSwitcher displays one view at a time and handles the lifecycle of view scopes
 * based on selection changes. Different implementations determine how state is managed
 * when switching between views:
 *
 * - [SingleScopeViewSwitcher]: Cancels the previous scope when switching, losing state
 * - [RetainingScopeViewSwitcher]: Retains all scopes, preserving state across switches
 *
 * ### Usage with Compose
 * Use [ViewSwitcherHost] to render the currently selected view:
 * ```kotlin
 * ViewSwitcherHost(switcher = viewSwitcher) { key, scope ->
 *     when (key) {
 *         TabRoute.Home -> homeViewProvider(scope)
 *         TabRoute.Settings -> settingsViewProvider(scope)
 *     }
 * }
 * ```
 *
 * @param K The type of [ViewKey] used to identify views.
 * @see SingleScopeViewSwitcher for single-view-at-a-time navigation
 * @see RetainingScopeViewSwitcher for tab-style navigation with retained state
 * @see ViewSwitcherHost for rendering in Compose
 */
interface ViewSwitcher<K : ViewKey> {
    /**
     * The currently selected key, or `null` if no view is selected.
     */
    val selected: K?

    /**
     * Changes the current selection.
     *
     * This triggers the switcher to update its state and potentially create or destroy
     * view scopes based on the implementation strategy.
     *
     * @param key The key to select, or `null` to deselect.
     */
    fun onSelect(key: K?)

    /**
     * Gets or creates the [ViewScopeProvider] for the currently selected key.
     *
     * If no key is selected, returns `null`. Otherwise, creates the view scope if it
     * doesn't exist (using the provided [content] factory) or returns the existing one.
     *
     * @param content Factory function that creates a [ViewProvider] for the given key and scope.
     * @return The [ViewScopeProvider] for the selected view, or `null` if nothing is selected.
     */
    fun getOrCreate(content: (K, ManagedCoroutineScope) -> ViewProvider): ViewScopeProvider?
}
