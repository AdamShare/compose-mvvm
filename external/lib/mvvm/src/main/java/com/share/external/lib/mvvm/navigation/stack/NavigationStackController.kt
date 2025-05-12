package com.share.external.lib.mvvm.navigation.stack

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.share.external.lib.mvvm.navigation.content.View
import com.share.external.lib.mvvm.navigation.content.Presentation
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.dialog.DialogContainer
import timber.log.Timber

@Composable
fun <V : View> NavigationStackHost(
    analyticsId: String,
    navigationStack: ViewModelNavigationStack<V>,
    defaultContent: @Composable () -> Unit,
) = navigationStack.run {
    val saveableStateHolder = rememberSaveableStateHolder()

    val visibleProviders = mutableMapOf<ViewModelStoreContentProvider<V>, Presentation>()
    var hasFullScreen = false

    for (provider in navigationStack.stack.values.asReversed()) {
        val displayMode = provider.content.preferredPresentation()
        visibleProviders[provider] = displayMode
        if (displayMode == Presentation.FullScreen) {
            hasFullScreen = true
            break
        }
    }

    if (!hasFullScreen) {
        defaultContent()
    }

    if (visibleProviders.isNotEmpty()) {
        BackHandler {
            pop()
        }
    }

    visibleProviders.keys.reversed().forEach { provider ->
        provider.LocalOwnersProvider(saveableStateHolder) {
            when (val displayMode = provider.content.preferredPresentation()) {
                Presentation.FullScreen -> {
                    provider.content.Content()
                }

                is Presentation.Overlay -> {
                    if (displayMode.properties != null) {
                        DialogContainer(
                            onDismiss = ::removeAll,
                            properties = displayMode.properties,
                            backgroundContent = null,
                            content = {
                                provider.content.Content()
                            }
                        )
                    } else {
                        provider.content.Content()
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