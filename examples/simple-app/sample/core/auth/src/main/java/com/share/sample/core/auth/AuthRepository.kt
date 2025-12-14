package com.share.sample.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing authentication state.
 *
 * Provides reactive auth state via [authState] StateFlow and methods to
 * login/logout. The state is persisted via [CredentialsStorage].
 */
class AuthRepository(
    private val credentialsStorage: CredentialsStorage
) {
    private val _authState = MutableStateFlow<AuthState>(
        if (credentialsStorage.hasCredentials()) {
            AuthState.LoggedIn(credentialsStorage.getUsername()!!)
        } else {
            AuthState.LoggedOut
        }
    )

    /** Observable authentication state. */
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /** Current auth state (non-reactive). */
    val currentState: AuthState get() = _authState.value

    /** Returns true if the user is currently logged in. */
    val isLoggedIn: Boolean get() = currentState is AuthState.LoggedIn

    /**
     * Attempts to log in with the given credentials.
     *
     * For this fake implementation, any non-empty username/password combination
     * is accepted. The credentials are stored for persistence.
     *
     * @return true if login was successful
     */
    fun login(username: String, password: String): Boolean {
        if (username.isBlank() || password.isBlank()) {
            return false
        }

        credentialsStorage.saveCredentials(Credentials(username, password))
        _authState.value = AuthState.LoggedIn(username)
        return true
    }

    /**
     * Logs out the current user, clearing stored credentials.
     */
    fun logout() {
        credentialsStorage.clearCredentials()
        _authState.value = AuthState.LoggedOut
    }
}
