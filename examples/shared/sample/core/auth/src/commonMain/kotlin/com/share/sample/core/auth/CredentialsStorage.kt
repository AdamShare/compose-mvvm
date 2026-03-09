package com.share.sample.core.auth

/**
 * Platform-agnostic interface for credentials storage.
 *
 * In a real app, this would use encrypted storage (EncryptedSharedPreferences on Android)
 * and proper security practices. This is simplified for demonstration purposes.
 */
interface CredentialsStorage {
    fun saveCredentials(credentials: Credentials)
    fun getUsername(): String?
    fun getPassword(): String?
    fun hasCredentials(): Boolean
    fun clearCredentials()
}
