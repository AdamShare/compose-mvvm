package com.share.sample.feature.onboarding.signin.signup

import com.getbackcompose.navigation.stack.NavigationStackEntry
import com.getbackcompose.navigation.stack.NavigationRouteFactory
import com.getbackcompose.navigation.stack.Screen
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Scope

@Scope @MustBeDocumented @Retention(value = AnnotationRetention.RUNTIME) annotation class SignUpScope

@SignUpScope
@Subcomponent(modules = [SignUpViewModelModule::class, SignUpViewModule::class])
interface SignUpComponent {
    val screen: SignUpScreen

    class Dependency(val navigationStackEntry: NavigationStackEntry<Screen>)

    @Subcomponent.Factory
    abstract class Factory : NavigationRouteFactory<NavigationStackEntry<Screen>, Screen> {
        override val name: String
            get() = "SignUp"

        abstract fun create(@BindsInstance dependency: Dependency): SignUpComponent

        override fun create(navigationStackEntry: NavigationStackEntry<Screen>): Screen {
            return create(Dependency(navigationStackEntry)).screen
        }
    }
}
