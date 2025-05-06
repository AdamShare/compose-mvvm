package com.share.dynamic.feature.signin

import com.share.dynamic.feature.signin.signup.SignUpComponent
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.ComposableProvider
import com.share.external.lib.mvvm.navigation.stack.NavigationBackStack
import com.share.external.lib.mvvm.navigation.stack.RootNavigationContext
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Provider
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class SignInScope

@SignInScope
@Subcomponent(modules = [SignInModule::class])
interface SignInComponent: Provider<SignInViewModel> {
    class Scope(
        actual: ManagedCoroutineScope
    ): ManagedCoroutineScope by actual

    @Subcomponent.Factory
    interface Factory: (Scope) -> SignInComponent {
        override operator fun invoke(@BindsInstance scope: Scope): SignInComponent
    }
}

@Module(
    subcomponents = [SignUpComponent::class]
)
object SignInModule {
    @SignInScope
    @Provides
    fun viewModel(
        navigationContext: RootNavigationContext<ComposableProvider>,
        signUp: SignUpComponent.Factory,
    ) = SignInViewModel(
        navigationContext = navigationContext,
        signUp = signUp,
    )

    @SignInScope
    @Provides
    fun signInNavigationController(
        scope: SignInComponent.Scope,
    ): SignInNavigationController = SignInNavigationController(
        scope
    )

    @SignInScope
    @Provides
    fun signInNavigationContext(
        navigationController: SignInNavigationController,
    ) = navigationController.rootContext()

    @SignInScope
    @Provides
    fun navigationBackStack(
        navigationController: SignInNavigationController,
    ): NavigationBackStack = navigationController

    @SignInScope
    @Provides
    fun signInView(
        navigationController: SignInNavigationController,
        viewModel: SignInViewModel,
    ) = SignInView(
        navigationController = navigationController,
        viewModel = viewModel,
    )
}

