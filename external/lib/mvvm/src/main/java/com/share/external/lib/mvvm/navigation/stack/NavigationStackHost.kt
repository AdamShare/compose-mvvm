package com.share.external.lib.mvvm.navigation.stack

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
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
    stack: ManagedCoroutineScopeStack<V, NavigationStackEntryViewProvider>,
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
 * This composable observes a [ManagedCoroutineScopeStack] and renders at most one full-screen view and one modal view at
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
    stack: List<NavigationStackEntryViewProvider>,
    onBack: () -> Unit,
    backHandlerEnabled: Boolean = true,
    defaultContent: @Composable () -> Unit,
) {
    val saveableStateHolder = rememberSaveableStateHolder()

    @Suppress("RememberReturnType")
    remember(saveableStateHolder, stack) {
        stack.forEach {
            it.scopedViewProvider.setSaveableStateHolder(saveableStateHolder)
        }
    }

    val visibleStack = arrayOfNulls<Pair<NavigationStackEntryViewProvider, ModalProperties?>>(size = 2)

    for (entry in stack.asReversed()) {
        when (val displayMode = entry.preferredPresentationStyle()) {
            ViewPresentation.Style.FullScreen -> {
                visibleStack[0] = (entry to null)
                break
            }
            is ViewPresentation.Style.Modal -> {
                if (visibleStack[1] == null) {
                    visibleStack[1] = (entry to displayMode.properties)
                }
            }
        }
    }

    if (visibleStack.isEmpty()) {
        defaultContent()
    } else if (backHandlerEnabled) {
        BackHandler(onBack = onBack)
    }

    visibleStack.forEach { entry ->
        val (provider, modalProperties) = entry ?: return@forEach

        key(provider.scopedViewProvider.id) {
            saveableStateHolder.SaveableStateProvider(key = provider.scopedViewProvider.id) {
                ModalContainer(
                    onDismiss = onBack,
                    properties = modalProperties,
                    content = provider.scopedViewProvider.view.content
                )
            }
        }
    }

    LaunchedEffect(stack) {
        logger.logEntries(entries = stack, name = name) {
            when (it) {
                visibleStack[0]?.first -> "FullScreen"
                visibleStack[1]?.first -> "Modal"
                else -> null
            }
        }
    }
}

private val logger = Logger.withTag("NavigationStackHost")
