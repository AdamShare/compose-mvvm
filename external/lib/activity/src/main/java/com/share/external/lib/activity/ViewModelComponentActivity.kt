package com.share.external.lib.activity

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.ViewModel
import com.share.external.lib.activity.application.ApplicationCoroutineScopeFactory
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.activity.compose.decorViewProperties
import com.share.external.lib.activity.compose.rememberActivityViewContext
import com.share.external.lib.activity.viewmodel.viewModel
import com.share.external.lib.compose.context.LocalViewContext
import com.share.external.lib.compose.foundation.layout.LocalDecorViewProperties
import com.share.external.lib.mvvm.base.ViewProvider
import com.share.external.lib.mvvm.navigation.lifecycle.VisibilityScopedView
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class Main(
    val application: KClass<*> = Application::class,
)

abstract class ViewModelComponentActivity<A: ApplicationCoroutineScopeFactory, V : ViewProvider> :
    ComponentActivity(),
    ActivityViewModelContentProvider<A, V> {
    private val componentHolderViewModel by viewModel {
        @Suppress("UNCHECKED_CAST")
        val application = (application as A)
        val scope = ManagedCoroutineScope(
            application.create(
                name = "ActivityViewModel",
                context = Dispatchers.IO
            )
        )
        val viewProvider = buildProvider(
            application = application,
            scope = scope
        )
        ComponentViewModel(
            content = VisibilityScopedView(
                scopeFactory = {
                    scope.create(
                        name = "ActivityView",
                        context = Dispatchers.Main.immediate
                    )
                },
                viewProvider = viewProvider
            ).content,
            scope = scope,
            viewProvider = viewProvider,
        )
    }

    val viewProvider get() = componentHolderViewModel.viewProvider

    private class ComponentViewModel<V>(
        val scope: ManagedCoroutineScope,
        val viewProvider: V,
        val content: @Composable () -> Unit,
    ) : ViewModel(
            AutoCloseable { scope.cancel(awaitChildrenComplete = false, message = "Activity destroyed") }
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
