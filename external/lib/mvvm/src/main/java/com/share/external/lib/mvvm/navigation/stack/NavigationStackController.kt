package com.share.external.lib.mvvm.navigation.stack

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.ComposableProvider
import com.share.external.lib.mvvm.navigation.content.DisplayMode
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.dialog.DialogContainer
import timber.log.Timber

/**
 * UI adapter for [ViewModelNavigationStack]. Should be hoisted to the highest
 * Compose node that represents a navigation host (e.g. Activity or flow entry point).
 */
@Stable
open class NavigationStackController<V : ComposableProvider>(
    val analyticsId: String,
    private val scope: ManagedCoroutineScope,
) : ViewModelNavigationStack<V>(scope) {
    fun rootContext() = RootNavigationContext(
        scope = scope,
        stack = this
    )

    @Composable
    fun Content(defaultContent: @Composable () -> Unit) {
        val saveableStateHolder = rememberSaveableStateHolder()

        val visibleProviders = mutableMapOf<ViewModelStoreContentProvider<V>, DisplayMode>()
        var hasFullScreen = false

        for (provider in stack.values.asReversed()) {
            val displayMode = provider.content.displayMode()
            visibleProviders[provider] = displayMode
            if (displayMode == DisplayMode.FullScreen) {
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
                when (val displayMode = provider.content.displayMode()) {
                    DisplayMode.FullScreen -> {
                        provider.content.Content()
                    }

                    is DisplayMode.Overlay -> {
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

        logBackstack(stack, visibleProviders)
    }

    private fun logBackstack(
        backstack: Map<NavigationKey, ViewModelStoreContentProvider<V>>,
        rendered: Map<ViewModelStoreContentProvider<V>, DisplayMode>,
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

    companion object {
        private const val TAG = "NavigationStackController"
    }
}