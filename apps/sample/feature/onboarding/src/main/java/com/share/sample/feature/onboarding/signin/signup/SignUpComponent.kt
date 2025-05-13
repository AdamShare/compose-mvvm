package com.share.sample.feature.signin.signup

import com.share.external.lib.mvvm.navigation.content.View
import com.share.external.lib.mvvm.navigation.stack.NavigationStackEntry
import com.share.external.lib.mvvm.navigation.stack.NavigationViewFactory
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
@Subcomponent(modules = [SignUpModule::class, SignUpViewModule::class])
interface SignUpComponent {
    val view: SignUpView

    class Scope(
        actual: NavigationStackEntry<View>
    ): NavigationStackEntry<View> by actual


    @Subcomponent.Factory
    abstract class Factory: NavigationViewFactory<View> {
        override val analyticsId: String get() = "SignUp"

        abstract fun create(@BindsInstance scope: Scope): SignUpComponent

        override fun invoke(scope: NavigationStackEntry<View>): View {
            return create(Scope(scope)).view
        }
    }
}

fun SignUpComponent.Factory.view(
    scope: NavigationStackEntry<View>
) = create(SignUpComponent.Scope(scope)).view

@Module
object SignUpModule {
    @SignUpScope
    @Provides
    fun viewModel(
        scope: SignUpComponent.Scope,
    ) = SignUpViewModel(
        scope,
    )
}


