package com.share.sample.app.main

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.sample.feature.onboarding.OnboardingComponent
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Scope

@Scope @MustBeDocumented @Retention(value = AnnotationRetention.RUNTIME) annotation class MainViewScope

@MainViewScope
@Subcomponent(modules = [MainViewModule::class])
interface MainViewComponent {
    val navigationController: MainViewNavigationController
    val onboarding: OnboardingComponent.Factory

    @Subcomponent.Factory
    abstract class Factory {
        abstract fun create(@BindsInstance scope: MainViewProviderScope): MainViewComponent

        operator fun invoke(parent: ManagedCoroutineScope): MainViewComponent {
            return create(scope = MainViewProviderScope(parent = parent))
        }
    }
}

class MainViewProviderScope(parent: ManagedCoroutineScope) :
    ManagedCoroutineScope by parent.childManagedScope(name = "MainViewProvider")
