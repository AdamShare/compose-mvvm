package com.share.subcomponent.feature.signin

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationComposableProvider
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.v1.NavigationStackController
import com.share.subcomponent.feature.signin.signup.SignUpComponent
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Provider
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class SignInScope

@SignInScope
@Subcomponent(modules = [SignInModule::class])
interface SignInComponent: Provider<SignInViewModel> {
    @Subcomponent.Factory
    interface Factory: () -> SignInComponent

    interface ParentScope: ManagedCoroutineScope
}

@Module(
    subcomponents = [SignUpComponent::class]
)
object SignInModule {
    @SignInScope
    @Provides
    fun managedCoroutineScope(
        parent: SignInComponent.ParentScope
    ) = SignInManagedCoroutineScope(parent)

    @SignInScope
    @Provides
    fun viewModel(
        navigationController: SignInNavigationController,
        scope: SignInManagedCoroutineScope,
        signUp: SignUpComponent.Factory,
    ) = SignInViewModel(
        navigationController = navigationController,
        scope = scope,
        signUp = signUp,
    )

    @SignInScope
    @Provides
    fun signInNavigationController(
        scope: SignInManagedCoroutineScope,
    ) = SignInNavigationController(
        scope.create("SignInNavigationController")
    )

    @SignInScope
    @Provides
    fun signUpComponentParentScope(
        o: SignInManagedCoroutineScope
    ): SignUpComponent.ParentScope = o
}

class SignInManagedCoroutineScope(
    parent: ManagedCoroutineScope
): ManagedCoroutineScope by parent.childManagedScope(
    name = "SignIn"
), SignUpComponent.ParentScope

class SignInNavigationController(
    scope: CoroutineScope,
): NavigationStackController<NavigationKey, NavigationComposableProvider>(
    analyticsId = "SignInNavigationController",
    scope = scope,
)