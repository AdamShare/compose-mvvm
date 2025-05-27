package com.share.sample.feature.onboarding

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.View
import com.share.sample.feature.onboarding.signin.SignInComponent
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

@Scope @MustBeDocumented @Retention(value = AnnotationRetention.RUNTIME) annotation class OnboardingScope

@OnboardingScope
@Subcomponent(modules = [OnboardingModule::class, OnboardingViewModule::class])
interface OnboardingComponent {
    val view: OnboardingView

    class Scope(actual: ManagedCoroutineScope) : ManagedCoroutineScope by actual

    @Subcomponent.Factory
    abstract class Factory {
        abstract fun create(@BindsInstance scope: Scope): OnboardingComponent

        operator fun invoke(scope: ManagedCoroutineScope): View {
            return create(Scope(scope)).view
        }
    }
}

@Module(subcomponents = [SignInComponent::class]) object OnboardingModule
