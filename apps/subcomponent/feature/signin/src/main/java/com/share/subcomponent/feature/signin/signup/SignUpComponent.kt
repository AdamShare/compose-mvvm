package com.share.subcomponent.feature.signin.signup

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Provider
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class SignUpScope

@SignUpScope
@Subcomponent(modules = [SignUpModule::class])
interface SignUpComponent:
    Provider<SignUpViewModel> {
        val view: SignUpView

    @Subcomponent.Factory
    interface Factory: () -> SignUpComponent

    interface ParentScope: ManagedCoroutineScope
}

@Module
object SignUpModule {
    @SignUpScope
    @Provides
    fun managedCoroutineScope(
        parent: SignUpComponent.ParentScope
    ) = SignUpManagedCoroutineScope(parent)

    @SignUpScope
    @Provides
    fun viewModel(
        scope: SignUpManagedCoroutineScope,
    ) = SignUpViewModel(
        scope,
    )

    @SignUpScope
    @Provides
    fun view(
        viewModelProvider: Provider<SignUpViewModel>,
    ) = SignUpView(
        viewModelProvider
    )
}

class SignUpManagedCoroutineScope(
    parent: ManagedCoroutineScope
): ManagedCoroutineScope by parent.childManagedScope(
    name = "SignUp"
)

