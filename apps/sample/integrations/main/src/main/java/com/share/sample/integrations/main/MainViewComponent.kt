package com.share.sample.integrations.main

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.sample.feature.main.MainTabComponent
import com.share.sample.feature.onboarding.OnboardingComponent
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class MainViewScope

@MainViewScope
@Subcomponent(modules = [MainViewModule::class])
interface MainViewComponent {
    val viewProvider: MainViewProvider

    @Subcomponent.Factory
    abstract class Factory {
        abstract fun create(@BindsInstance scope: MainViewProviderScope): MainViewComponent

        operator fun invoke(coroutineScope: CoroutineScope): MainViewComponent {
            return create(scope = MainViewProviderScope(actual = coroutineScope))
        }
    }
}

class MainViewProviderScope(actual: CoroutineScope) : ManagedCoroutineScope by ManagedCoroutineScope(actual = actual)
