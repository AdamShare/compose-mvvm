package com.share.external.lib.activity

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.ViewModel
import com.share.external.lib.activity.application.ApplicationCoroutineScopeFactory
import com.share.external.foundation.coroutines.childSupervisorJobScope
import com.share.external.lib.activity.compose.decorViewProperties
import com.share.external.lib.activity.compose.rememberActivityViewContext
import com.share.external.lib.activity.viewmodel.viewModel
import com.share.external.lib.view.context.LocalViewContext
import com.share.external.lib.compose.foundation.layout.LocalDecorViewProperties
import com.share.external.lib.view.ViewProvider
import com.share.external.lib.view.VisibilityScopedView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.reflect.KClass

/**
 * Annotation to mark the main activity entry point.
 *
 * Used to identify the primary activity that should be launched when the application starts.
 *
 * @param application The Application class to associate with this activity.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class Main(
    val application: KClass<*> = Application::class,
)

/**
 * Base activity class that integrates Dagger component creation with AndroidX ViewModel lifecycle.
 *
 * This class provides a pattern for:
 * - Creating a Dagger component tied to the Activity's ViewModel lifecycle (survives configuration changes)
 * - Automatically providing [LocalDecorViewProperties] and [LocalViewContext] composition locals
 * - Managing coroutine scopes that cancel when the Activity finishes (not on config changes)
 *
 * ### Lifecycle
 * The [viewProvider] is created once per logical Activity instance (survives rotation) and its
 * coroutine scope cancels only when the Activity is finishing (not during configuration changes).
 *
 * ### Usage
 * ```kotlin
 * class MainActivity : ViewModelComponentActivity<MyApplication, MainViewProvider>() {
 *     override fun buildProvider(
 *         application: MyApplication,
 *         coroutineScope: CoroutineScope
 *     ): MainViewProvider {
 *         return application.mainComponent()
 *             .mainViewComponentFactory()
 *             .invoke(coroutineScope)
 *             .viewProvider
 *     }
 * }
 * ```
 *
 * @param A The Application type that implements [ApplicationCoroutineScopeFactory].
 * @param V The ViewProvider type that this activity hosts.
 */
abstract class ViewModelComponentActivity<A: ApplicationCoroutineScopeFactory, V : ViewProvider> : ComponentActivity(),
    ActivityViewModelContentProvider<A, V> {
    private val componentHolderViewModel by viewModel {
        @Suppress("UNCHECKED_CAST")
        val application = (application as A)
        val coroutineScope = application.create(
            name = "ActivityViewModel",
            context = Dispatchers.Default
        )
        val viewProvider = buildProvider(
            application = application,
            coroutineScope = coroutineScope
        )
        ComponentViewModel(
            content = VisibilityScopedView(
                scopeFactory = {
                    coroutineScope.childSupervisorJobScope(
                        name = "ActivityView",
                        context = Dispatchers.Main.immediate
                    )
                },
                viewProvider = viewProvider
            ).content,
            coroutineScope = coroutineScope,
            viewProvider = viewProvider,
        )
    }

    val viewProvider get() = componentHolderViewModel.viewProvider

    private class ComponentViewModel<V>(
        val coroutineScope: CoroutineScope,
        val viewProvider: V,
        val content: @Composable () -> Unit,
    ) : ViewModel(
            AutoCloseable {
                coroutineScope.cancel(message = "Activity finished")
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalDecorViewProperties provides decorViewProperties(),
                LocalViewContext provides rememberActivityViewContext(),
                content = componentHolderViewModel.content
            )
        }
    }
}
