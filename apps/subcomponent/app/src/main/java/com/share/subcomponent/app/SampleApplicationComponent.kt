package com.share.subcomponent.app

import android.app.Application
import com.share.external.lib.mvvm.Injectable
import com.share.external.lib.mvvm.application.ApplicationCoroutineScopeModule
import com.share.subcomponent.app.activity.SampleActivityViewModelComponent
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [Application::class],
    modules = [SampleApplicationModule::class]
)
interface SampleApplicationComponent : Injectable<SampleApplication> {
    @Component.Factory
    interface Factory : (Application) -> SampleApplicationComponent
}

@Module(
    subcomponents = [
        SampleActivityViewModelComponent::class,
    ],
    includes = [
        ApplicationCoroutineScopeModule::class
    ],
)
object SampleApplicationModule

