package com.share.sample.app.main

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.foundation.coroutines.childSupervisorJobScope
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
interface MainViewComponent: MainViewDependency {
    @Subcomponent.Factory
    abstract class Factory {
        abstract fun create(@BindsInstance scope: MainViewProviderScope): MainViewComponent

        operator fun invoke(coroutineScope: CoroutineScope): MainViewComponent {
            return create(scope = MainViewProviderScope(actual = coroutineScope))
        }
    }
}

class MainViewProviderScope(actual: CoroutineScope) : ManagedCoroutineScope by ManagedCoroutineScope(actual = actual)
