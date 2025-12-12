package com.share.sample.feature.onboarding.signin

import com.share.external.lib.navigation.stack.NavigationStackEntry
import com.share.external.lib.navigation.stack.NavigationRouteFactory
import com.share.external.lib.navigation.stack.Screen
import com.share.sample.core.auth.AuthRepository
import com.share.sample.feature.onboarding.signin.signup.SignUpComponent
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Qualifier
import javax.inject.Scope

@Scope @MustBeDocumented @Retention(value = AnnotationRetention.RUNTIME) annotation class SignInScope

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class SignIn

@SignInScope
@Subcomponent(modules = [SignInModule::class, SignInViewModule::class])
interface SignInComponent {
    val viewProvider: SignInViewProvider

    class Dependency(val navigationStackEntry: NavigationStackEntry<Screen>)

    @Subcomponent.Factory
    abstract class Factory : NavigationRouteFactory<NavigationStackEntry<Screen>, Screen> {
        override val name: String
            get() = "SignIn"

        abstract fun create(@BindsInstance dependency: Dependency): SignInComponent

        override fun invoke(scope: NavigationStackEntry<Screen>): Screen {
            return create(Dependency(scope)).viewProvider
        }
    }
}

@Module(subcomponents = [SignUpComponent::class])
object SignInModule {
    @SignInScope @Provides fun viewModel(
        dependency: SignInComponent.Dependency
    ) = EmailViewModel(
        scope = dependency.navigationStackEntry
    )

    @SignInScope @Provides fun signInViewModel(
        emailViewModel: EmailViewModel,
        authRepository: AuthRepository
    ) = SignInViewModel(emailViewModel = emailViewModel, authRepository = authRepository)
}
