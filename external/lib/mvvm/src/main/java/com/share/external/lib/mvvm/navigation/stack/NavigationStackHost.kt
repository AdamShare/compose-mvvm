package com.share.external.lib.mvvm.navigation.stack

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.share.external.lib.mvvm.navigation.content.View
import com.share.external.lib.mvvm.navigation.content.Presentation
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.dialog.DialogContainer
import timber.log.Timber

/**
 * Hosts a navigation stack of [View]s and renders the appropriate content based on their [Presentation] mode.
 *
 * This composable is responsible for rendering a stack of views managed by a [ViewModelNavigationStack].
 * Each view is associated with a [ViewModelStoreContentProvider], which controls the view’s lifecycle and
 * presentation. Views can be presented in either full screen or overlay mode.
 *
 * Behavior:
 * - Renders all currently visible views in the stack in reverse order (i.e., top of the stack is drawn last).
 * - Displays a [defaultContent] composable underneath the stack if no full-screen content is present.
 * - Handles back navigation using [BackHandler], popping the stack if [backHandlerEnabled] is true and the stack is not empty.
 * - Uses [rememberSaveableStateHolder] to preserve state across recompositions for each view.
 * - Supports overlay presentation via [DialogContainer] if the view prefers an overlay presentation mode with dialog properties.
 *
 * @param analyticsId A string identifier used to log changes to the visible backstack.
 * @param navigationStack The stack of views to render, maintained via a [ViewModelNavigationStack].
 * @param backHandlerEnabled Enables hardware/system back button support for stack popping if true.
 * @param defaultContent Composable content shown underneath the view stack if no full-screen views are present.
 *
 * @see View
 * @see Presentation
 * @see DialogContainer
 * @see ViewModelNavigationStack
 * @see ViewModelStoreContentProvider
 */
@Composable
fun <V : View> NavigationStackHost(
    analyticsId: String,
    navigationStack: ViewModelNavigationStack<V>,
    backHandlerEnabled: Boolean = true,
    defaultContent: @Composable () -> Unit,
) = navigationStack.run {
    val saveableStateHolder = rememberSaveableStateHolder()

    val visibleProviders = mutableMapOf<ViewModelStoreContentProvider<V>, Presentation>()
    var hasFullScreen = false

    for (provider in navigationStack.stack.values.asReversed()) {
        val displayMode = provider.view.preferredPresentation()
        visibleProviders[provider] = displayMode
        if (displayMode == Presentation.FullScreen) {
            hasFullScreen = true
            break
        }
    }

    if (!hasFullScreen) {
        defaultContent()
    }

    if (backHandlerEnabled && visibleProviders.isNotEmpty()) {
        BackHandler {
            pop()
        }
    }

    visibleProviders.keys.reversed().forEach { provider ->
        provider.LocalOwnersProvider(saveableStateHolder) {
            when (val displayMode = provider.view.preferredPresentation()) {
                Presentation.FullScreen -> {
                    provider.view.content()
                }

                is Presentation.Overlay -> {
                    if (displayMode.properties != null) {
                        DialogContainer(
                            onDismiss = ::pop,
                            properties = displayMode.properties,
                            backgroundContent = null,
                            content = {
                                provider.view.content()
                            }
                        )
                    } else {
                        provider.view.content()
                    }
                }
            }
        }
    }

    logBackstack(analyticsId, stack, visibleProviders)
}

private fun <V> logBackstack(
    analyticsId: String,
    backstack: Map<NavigationKey, ViewModelStoreContentProvider<V>>,
    rendered: Map<ViewModelStoreContentProvider<V>, Presentation>,
) {
    Timber.tag(TAG).d("%s", object {
        override fun toString(): String = buildString {
            append("Backstack $analyticsId[")
            backstack.entries.forEachIndexed { i, (key, provider) ->
                append("{${key.analyticsId}")
                rendered[provider]?.let { append(": ${it.javaClass.simpleName}") }
                append("}")
                if (i < backstack.size - 1) append(" ⇨ ")
            }
            append("]")
        }
    })
}

private const val TAG = "NavigationStackHost"