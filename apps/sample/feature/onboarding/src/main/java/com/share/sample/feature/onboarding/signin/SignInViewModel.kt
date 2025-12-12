package com.share.sample.feature.onboarding.signin

import com.share.external.lib.compose.state.ViewModel
import com.share.external.lib.navigation.stack.NavigationStackEntry
import com.share.external.lib.navigation.stack.Screen
import com.share.sample.core.auth.AuthRepository

/**
 * ViewModel for the sign-in screen.
 *
 * Handles the sign-in action using the AuthRepository.
 */
class SignInViewModel(
    private val authRepository: AuthRepository,
    private val emailViewModel: EmailViewModel,
) {
    /**
     * Attempts to sign in with the current email.
     * For demo purposes, uses the email as both username and a fixed password.
     */
    fun signIn() {
        val email = emailViewModel.email
        if (email.isNotBlank() && !emailViewModel.emailHasErrors) {
            // For demo purposes, use the email as username with a fixed password
            authRepository.login(email, "password")
        }
    }
}
