package com.share.sample.feature.onboarding.signin.signup

import com.share.external.lib.mvvm.navigation.content.Screen
import com.share.external.lib.mvvm.navigation.stack.NavigationStackEntry
import com.share.external.lib.mvvm.navigation.stack.NavigationViewFactory
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope @MustBeDocumented @Retention(value = AnnotationRetention.RUNTIME) annotation class SignUpScope

@SignUpScope
@Subcomponent(modules = [SignUpViewModelModule::class, SignUpViewModule::class])
interface SignUpComponent {
    val screen: SignUpScreen

    class Scope(actual: NavigationStackEntry<Screen>) : NavigationStackEntry<Screen> by actual

    @Subcomponent.Factory
    abstract class Factory : NavigationViewFactory<Screen> {
        override val name: String
            get() = "SignUp"

        abstract fun create(@BindsInstance scope: Scope): SignUpComponent

        override fun invoke(scope: NavigationStackEntry<Screen>): Screen {
            return create(Scope(scope)).screen
        }
    }
}
