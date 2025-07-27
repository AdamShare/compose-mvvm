package com.share.external.lib.mvvm.navigation.stack

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import co.touchlab.kermit.Logger
import com.share.external.lib.compose.modal.ModalContainer
import com.share.external.lib.compose.modal.ModalProperties
import com.share.external.lib.core.View
import com.share.external.lib.core.ViewProvider
import com.share.external.lib.mvvm.navigation.content.ViewPresentation

@Composable
fun <V> NavigationStackHost(
    name: String,
    stack: ViewModelNavigationStack<V>,
    backHandlerEnabled: Boolean = true,
    defaultContent: @Composable () -> Unit,
) where V : ViewProvider, V : ViewPresentation {
    NavigationStackHost(
        name = name,
        stack = stack.stack,
        onBack = stack::pop,
        backHandlerEnabled = backHandlerEnabled,
        defaultContent = defaultContent,
    )
}

/**
 * Hosts a navigation stack of [View]s and renders the appropriate content based on their [ViewPresentation.Style].
 *
 * This composable observes a [ViewModelNavigationStack] and renders at most one full-screen view and one modal view at
 * any given time. Views are presented in order from the top of the stack (i.e., most recently pushed) and updated
 * automatically as the stack changes.
 *
 * ### Behavior
 * - Renders the first view in the stack marked as [ViewPresentation.Style.FullScreen].
 * - Renders the first view in the stack marked as [ViewPresentation.Style.Modal], **if it appears before** the
 *   full-screen view. Modal content is layered above the full-screen or [defaultContent].
 * - If no full-screen view is found, [defaultContent] is displayed.
 * - Supports modal presentation with layout and interaction behavior defined via [ModalProperties].
 * - Handles system back navigation via [BackHandler] when [backHandlerEnabled] is true.
 * - Preserves state across recompositions for all rendered views using [rememberSaveableStateHolder].
 * - Logs view appearance state changes using [name] for debugging or analytics purposes.
 */
@Composable
fun NavigationStackHost(
    name: String,
    stack: List<ViewPresentationScopedViewProvider>,
    onBack: () -> Unit,
    backHandlerEnabled: Boolean = true,
    defaultContent: @Composable () -> Unit,
) {
    val saveableStateHolder = rememberSaveableStateHolder()

    val visibleStack = mutableMapOf<ViewPresentationScopedViewProvider, ModalProperties?>()

    for (provider in stack.asReversed()) {
        when (val displayMode = provider.preferredPresentationStyle()) {
            ViewPresentation.Style.FullScreen -> {
                visibleStack[provider] = null
                break
            }
            is ViewPresentation.Style.Modal -> {
                if (visibleStack.isEmpty()) {
                    visibleStack[provider] = displayMode.properties
                }
            }
        }
    }

    if (visibleStack.isEmpty()) {
        defaultContent()
    } else if (backHandlerEnabled) {
        BackHandler(onBack = onBack)
    }

    visibleStack.forEach {
        key(it.key) {
            ModalContainer(onDismiss = onBack, properties = it.value) {
                it.key.content(saveableStateHolder)
            }
        }
    }

    LaunchedEffect(stack) {
        logger.logEntries(entries = stack, name = name) {
            if (visibleStack.contains(it)) {
                visibleStack[it]?.let { "Modal" } ?: "FullScreen"
            } else null
        }
    }
}

private val logger = Logger.withTag("NavigationStackHost")
