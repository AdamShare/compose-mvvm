package com.share.sample.feature.signin.signup

import com.share.external.lib.mvvm.navigation.content.ComposableProvider
import com.share.external.lib.mvvm.navigation.stack.NavigationStackEntry
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class SignUpScope

@SignUpScope
@Subcomponent(modules = [SignUpModule::class])
interface SignUpComponent {
    val view: SignUpView

    @Subcomponent.Factory
    interface Factory {
        operator fun invoke(@BindsInstance scope: SignUpNavigationStackEntry): SignUpComponent
    }
}

@Module
object SignUpModule {
    @SignUpScope
    @Provides
    fun viewModel(
        scope: SignUpNavigationStackEntry,
    ) = SignUpViewModel(
        scope,
    )

    @SignUpScope
    @Provides
    fun view(
        viewModel: SignUpViewModel,
    ) = SignUpView(
        viewModel = viewModel
    )
}

class SignUpNavigationStackEntry(
    actual: NavigationStackEntry<ComposableProvider>
): NavigationStackEntry<ComposableProvider> by actual

