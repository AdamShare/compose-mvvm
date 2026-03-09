package com.share.sample.core.auth

/**
 * In-memory implementation of credentials storage.
 *
 * Credentials are lost when the application exits.
 * In a real app, this could use a file-based or database-backed storage.
 */
class InMemoryCredentialsStorage : CredentialsStorage {
    @kotlin.concurrent.Volatile
    private var storedCredentials: Credentials? = null

    override fun saveCredentials(credentials: Credentials) {
        storedCredentials = credentials
    }

    override fun getUsername(): String? = storedCredentials?.username

    override fun getPassword(): String? = storedCredentials?.password

    override fun hasCredentials(): Boolean = storedCredentials != null

    override fun clearCredentials() {
        storedCredentials = null
    }
}
