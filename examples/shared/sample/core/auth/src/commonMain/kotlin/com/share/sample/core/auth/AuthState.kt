package com.share.sample.core.auth

/**
 * Represents the current authentication state of the user.
 */
sealed interface AuthState {
    /** User is not authenticated. */
    data object LoggedOut : AuthState

    /** User is authenticated with the given username. */
    data class LoggedIn(val username: String) : AuthState
}

/**
 * User credentials for authentication.
 */
data class Credentials(
    val username: String,
    val password: String
)
