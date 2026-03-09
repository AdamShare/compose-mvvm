package com.share.sample.core.auth

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/**
 * Android implementation of credentials storage using SharedPreferences.
 *
 * In a real app, this would use encrypted storage (EncryptedSharedPreferences)
 * and proper security practices. This is simplified for demonstration purposes.
 */
class AndroidCredentialsStorage(
    application: Application
) : CredentialsStorage {
    private val prefs: SharedPreferences = application.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override fun saveCredentials(credentials: Credentials) {
        prefs.edit()
            .putString(KEY_USERNAME, credentials.username)
            .putString(KEY_PASSWORD, credentials.password)
            .apply()
    }

    override fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    override fun getPassword(): String? = prefs.getString(KEY_PASSWORD, null)

    override fun hasCredentials(): Boolean = getUsername() != null && getPassword() != null

    override fun clearCredentials() {
        prefs.edit()
            .remove(KEY_USERNAME)
            .remove(KEY_PASSWORD)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "sample_auth_prefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
    }
}
