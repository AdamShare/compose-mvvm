package com.share.sample.app

import android.app.Application
import com.share.external.lib.activity.application.ApplicationCoroutineScopeFactory
import com.share.sample.app.main.MainViewComponent
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [Application::class, ApplicationCoroutineScopeFactory::class],
    modules = [SampleApplicationModule::class]
)
interface SampleApplicationComponent {
    @Component.Factory
    interface Factory : (Application, ApplicationCoroutineScopeFactory) -> SampleApplicationComponent

    fun inject(instance: SampleApplication)
}

@Module(subcomponents = [MainViewComponent::class])
object SampleApplicationModule
