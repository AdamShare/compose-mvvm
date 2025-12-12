package com.share.sample.feature.onboarding.signin.signup

import com.share.external.lib.navigation.stack.NavigationStackEntry
import com.share.external.lib.navigation.stack.NavigationRouteFactory
import com.share.external.lib.navigation.stack.Screen
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

        override fun invoke(navigationStackEntry: NavigationStackEntry<Screen>): Screen {
            return create(Dependency(navigationStackEntry)).screen
        }
    }
}
