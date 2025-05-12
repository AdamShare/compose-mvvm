package com.share.sample.feature.signin

import com.share.sample.feature.signin.signup.SignUpComponent
import com.share.external.lib.mvvm.navigation.content.View
import com.share.external.lib.mvvm.navigation.stack.NavigationStackEntry
import com.share.external.lib.mvvm.navigation.stack.NavigationStackScope
import com.share.external.lib.mvvm.navigation.stack.NavigationViewFactory
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Qualifier
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class SignInScope

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class SignIn

@SignInScope
@Subcomponent(modules = [
    SignInModule::class,
    SignInViewModule::class
])
interface SignInComponent {
    val view: SignInView

    class Scope(
        actual: NavigationStackScope<View>
    ): NavigationStackScope<View> by actual

    @Subcomponent.Factory
    abstract class Factory: NavigationViewFactory<View> {
        override val analyticsId: String get() = "SignIn"

        abstract fun create(@BindsInstance scope: Scope): SignInComponent

        override fun invoke(scope: NavigationStackEntry<View>): View {
            return create(Scope(scope)).view
        }
    }
}

@Module(
    subcomponents = [SignUpComponent::class]
)
object SignInModule {
    @SignInScope
    @Provides
    fun viewModel(
        scope: SignInComponent.Scope,
    ) = EmailViewModel(
        scope = scope,
    )
}

