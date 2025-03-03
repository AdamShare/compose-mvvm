package com.share.subcomponent.feature.signin

import dagger.Module
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class SignInScope

@SignInScope
@Subcomponent(modules = [SignInModule::class])
interface SignInComponent  {

    @Subcomponent.Factory
    interface Factory: () -> SignInComponent

    interface ParentCoroutineScope: CoroutineScope
}

@Module
object SignInModule

