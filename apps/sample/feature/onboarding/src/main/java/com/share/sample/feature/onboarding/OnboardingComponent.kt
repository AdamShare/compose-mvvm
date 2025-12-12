package com.share.sample.feature.onboarding

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.ViewProvider
import com.share.sample.feature.onboarding.signin.SignInComponent
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

@Scope @MustBeDocumented @Retention(value = AnnotationRetention.RUNTIME) annotation class OnboardingScope

@OnboardingScope
@Subcomponent(modules = [OnboardingModule::class, OnboardingViewModule::class])
interface OnboardingComponent {
    val viewProvider: OnboardingViewProvider

    class Dependency(val scope: ManagedCoroutineScope)

    @Subcomponent.Factory
    abstract class Factory: (ManagedCoroutineScope) -> ViewProvider  {
        abstract fun create(@BindsInstance dependency: Dependency): OnboardingComponent

        override operator fun invoke(scope: ManagedCoroutineScope): ViewProvider {
            return create(Dependency(scope)).viewProvider
        }
    }
}

@Module(subcomponents = [SignInComponent::class]) object OnboardingModule
