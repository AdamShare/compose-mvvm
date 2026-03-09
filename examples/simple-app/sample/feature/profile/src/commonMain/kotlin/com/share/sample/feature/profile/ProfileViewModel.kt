package com.share.sample.feature.profile

import com.getbackcompose.foundation.coroutines.CoroutineScopeFactory
import com.getbackcompose.compose.state.ViewModel
import com.share.sample.core.auth.AuthRepository
import com.share.sample.core.auth.AuthState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the profile screen.
 *
 * Exposes the current user info and provides logout functionality.
 */
class ProfileViewModel(
    scopeFactory: CoroutineScopeFactory,
    private val authRepository: AuthRepository
) : ViewModel(name = TAG, scopeFactory = scopeFactory) {

    /**
     * The current username, or null if not logged in.
     */
    fun username(): StateFlow<String?> =
        authRepository.authState
            .map { state ->
                when (state) {
                    is AuthState.LoggedIn -> state.username
                    AuthState.LoggedOut -> null
                }
            }
            .stateIn(scope, SharingStarted.WhileSubscribed(), null)

    /**
     * Logs out the current user.
     */
    fun logout() {
        authRepository.logout()
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
