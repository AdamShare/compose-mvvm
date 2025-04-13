package com.share.subcomponent.feature.signin.signup

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.NavStackEntry
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
interface SignUp:
    Provider<SignUpViewModel>,
    NavStackEntry<SignUpManagedCoroutineScope> {

    @Subcomponent.Factory
    interface Factory: () -> SignUp

    interface ParentScope: ManagedCoroutineScope
}

@Module
object SignUpModule {
    @Provides
    fun managedCoroutineScope(
        parent: SignUp.ParentScope
    ) = SignUpManagedCoroutineScope(parent)

    @Provides
    fun viewModel(
        scope: SignUpManagedCoroutineScope,
    ) = SignUpViewModel(
        scope,
    )
}

class SignUpManagedCoroutineScope(
    actual: ManagedCoroutineScope
): ManagedCoroutineScope by actual