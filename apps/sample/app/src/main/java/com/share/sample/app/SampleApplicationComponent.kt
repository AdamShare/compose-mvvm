package com.share.sample.app

import android.app.Application
import com.share.external.lib.mvvm.application.ApplicationCoroutineScopeModule
import com.share.external.lib.mvvm.application.Injectable
import com.share.sample.app.activity.SampleActivityViewModelComponent
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(dependencies = [Application::class], modules = [SampleApplicationModule::class])
interface SampleApplicationComponent : Injectable<SampleApplication> {
    @Component.Factory interface Factory : (Application) -> SampleApplicationComponent
}

@Module(subcomponents = [SampleActivityViewModelComponent::class], includes = [ApplicationCoroutineScopeModule::class])
object SampleApplicationModule
