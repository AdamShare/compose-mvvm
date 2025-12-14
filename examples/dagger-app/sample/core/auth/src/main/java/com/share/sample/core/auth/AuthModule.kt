package com.share.sample.core.auth

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AuthModule {
    @Singleton
    @Provides
    fun credentialsStorage(application: Application) = CredentialsStorage(application)

    @Singleton
    @Provides
    fun authRepository(credentialsStorage: CredentialsStorage) = AuthRepository(credentialsStorage)
}
