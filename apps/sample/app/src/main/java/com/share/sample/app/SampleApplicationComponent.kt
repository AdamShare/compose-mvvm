package com.share.sample.app

import android.app.Application
import com.share.external.lib.activity.application.ApplicationCoroutineScopeProvider
import com.share.external.lib.activity.inject.Injectable
import com.share.sample.app.main.MainViewComponent
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [Application::class, ApplicationCoroutineScopeProvider::class],
    modules = [SampleApplicationModule::class]
)
interface SampleApplicationComponent : Injectable<SampleApplication> {
    @Component.Factory
    interface Factory : (Application, ApplicationCoroutineScopeProvider) -> SampleApplicationComponent
}

@Module(subcomponents = [MainViewComponent::class])
object SampleApplicationModule
