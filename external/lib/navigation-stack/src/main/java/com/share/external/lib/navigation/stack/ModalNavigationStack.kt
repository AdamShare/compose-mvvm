package com.share.external.lib.navigation.stack

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.ViewPresentation
import com.share.external.lib.view.ViewProvider
import com.share.external.lib.view.ViewScopeProvider

/**
 * A navigation stack that supports both full-screen views and modal overlays.
 *
 * This stack manages views that implement both [ViewProvider] and [ViewPresentation], allowing
 * each view to specify whether it should be rendered as a full-screen destination or a modal
 * overlay via [ViewPresentation.preferredPresentationStyle].
 *
 * ### Modal Presentation
 * Views returning [ViewPresentation.Style.Modal] are rendered as overlays on top of the
 * underlying full-screen content, with configurable layout and dismissal behavior via
 * [com.share.external.lib.compose.modal.ModalProperties].
 *
 * ### Usage
 * ```kotlin
 * val navigationStack = ModalNavigationStack<Screen>(rootScope)
 *
 * // Push a full-screen view
 * navigationStack.push(HomeScreenFactory) { ... }
 *
 * // Push a modal
 * navigationStack.push(SettingsModalFactory) { ... }
 * ```
 *
 * @param V The view type, which must implement both [ViewProvider] and [ViewPresentation].
 * @param rootScope The parent scope that owns this navigation stack's lifecycle.
 * @param viewScopeProviderFactory Factory for creating [ViewScopeProvider] instances for each view.
 * @param initialStack Optional lambda to prepopulate the stack during construction.
 *
 * @see NavigationStackHost for rendering this stack in Compose
 * @see Screen for the typical view type used with this stack
 */
class ModalNavigationStack<V>(
    rootScope: ManagedCoroutineScope,
    viewScopeProviderFactory: ViewScopeProvider.Factory = ViewScopeProvider.Factory.Default,
    initialStack: (NavigationStack<V>) -> Unit = {},
): ManagedCoroutineScopeStack<V, NavigationStackEntryViewProvider>(
    rootScope = rootScope,
    entryFactory = { key, scope, viewProvider ->
        NavigationStackEntryViewProviderImpl(
            navigationKey = key,
            viewProvider = viewProvider,
            scopedViewProvider = viewScopeProviderFactory(
                name = key.name,
                onViewAppear = viewProvider::onViewAppear,
                scope = scope
            ),
        )
    },
    initialStack = initialStack,
) where V: ViewProvider, V: ViewPresentation
