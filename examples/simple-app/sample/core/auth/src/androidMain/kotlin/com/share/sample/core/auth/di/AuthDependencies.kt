package com.share.sample.core.auth.di

import android.app.Application
import com.share.sample.core.auth.AndroidCredentialsStorage
import com.share.sample.core.auth.AuthRepository
import com.share.sample.core.auth.CredentialsStorage

/**
 * Manual dependency provider for auth module (simple-app).
 * Creates instances of auth-related dependencies.
 */
object AuthDependencies {

    fun provideCredentialsStorage(application: Application): CredentialsStorage {
        return AndroidCredentialsStorage(application)
    }

    fun provideAuthRepository(application: Application): AuthRepository {
        return AuthRepository(provideCredentialsStorage(application))
    }
}
