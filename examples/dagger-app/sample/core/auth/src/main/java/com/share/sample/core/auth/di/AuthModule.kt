package com.share.sample.core.auth.di

import android.app.Application
import com.share.sample.core.auth.AndroidCredentialsStorage
import com.share.sample.core.auth.AuthRepository
import com.share.sample.core.auth.CredentialsStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AuthModule {
    @Singleton
    @Provides
    fun credentialsStorage(application: Application): CredentialsStorage =
        AndroidCredentialsStorage(application)

    @Singleton
    @Provides
    fun authRepository(credentialsStorage: CredentialsStorage) =
        AuthRepository(credentialsStorage)
}
