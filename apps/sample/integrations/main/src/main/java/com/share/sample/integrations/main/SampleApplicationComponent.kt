package com.share.sample.integrations.main

import android.app.Application
import com.share.external.lib.activity.application.ApplicationCoroutineScopeFactory
import com.share.sample.core.auth.AuthModule
import com.share.sample.core.auth.AuthRepository
import com.share.sample.core.data.DataModule
import com.share.sample.core.data.repository.FavoritesRepository
import com.share.sample.core.data.repository.FeedRepository
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [Application::class, ApplicationCoroutineScopeFactory::class],
    modules = [SampleApplicationModule::class, AuthModule::class, DataModule::class]
)
interface SampleApplicationComponent {
    @Component.Factory
    interface Factory : (Application, ApplicationCoroutineScopeFactory) -> SampleApplicationComponent

    val mainViewComponentFactory: MainViewComponent.Factory
    val authRepository: AuthRepository
    val feedRepository: FeedRepository
    val favoritesRepository: FavoritesRepository
}

@Module(subcomponents = [MainViewComponent::class])
object SampleApplicationModule
