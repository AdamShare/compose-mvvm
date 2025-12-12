package com.share.sample.core.auth

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/**
 * Fake credentials storage using SharedPreferences.
 *
 * In a real app, this would use encrypted storage (EncryptedSharedPreferences)
 * and proper security practices. This is simplified for demonstration purposes.
 */
class CredentialsStorage(
    application: Application
) {
    private val prefs: SharedPreferences = application.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun saveCredentials(credentials: Credentials) {
        prefs.edit()
            .putString(KEY_USERNAME, credentials.username)
            .putString(KEY_PASSWORD, credentials.password)
            .apply()
    }

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun getPassword(): String? = prefs.getString(KEY_PASSWORD, null)

    fun hasCredentials(): Boolean = getUsername() != null && getPassword() != null

    fun clearCredentials() {
        prefs.edit()
            .remove(KEY_USERNAME)
            .remove(KEY_PASSWORD)
            .apply()
    }

    /**
     * Validates the given credentials against stored credentials.
     * Returns true if credentials match or if no credentials are stored (first login).
     */
    fun validateCredentials(username: String, password: String): Boolean {
        val storedUsername = getUsername()
        val storedPassword = getPassword()

        // If no credentials stored, any login is valid (first time setup)
        if (storedUsername == null || storedPassword == null) {
            return true
        }

        return storedUsername == username && storedPassword == password
    }

    companion object {
        private const val PREFS_NAME = "sample_auth_prefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
    }
}
