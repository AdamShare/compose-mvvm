package com.share.subcomponent.feature.signin

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.subcomponent.feature.signin.signup.SignUp
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
    @Subcomponent.Factory
    interface Factory: () -> SignInComponent

    interface ParentScope: ManagedCoroutineScope
}

@Module
object SignInModule {
    @Provides
    fun managedCoroutineScope(
        parent: SignInComponent.ParentScope
    ) = SignInManagedCoroutineScope(parent)

    @Provides
    fun viewModel(
        scope: SignInManagedCoroutineScope,
    ) = SignInViewModel(
        scope,
    )
}

class SignInManagedCoroutineScope(actual: ManagedCoroutineScope):
    ManagedCoroutineScope by actual,
    SignUp.ParentScope