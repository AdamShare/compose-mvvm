package com.share.sample.feature.onboarding.signin.signup

import com.share.external.foundation.coroutines.CoroutineScopeFactory
import com.share.external.lib.compose.state.ViewModel
import com.share.sample.core.auth.AuthRepository
import dagger.Module
import dagger.Provides

@Module
object SignUpViewModelModule {
    @SignUpScope
    @Provides
    fun viewModel(dependency: SignUpComponent.Dependency, authRepository: AuthRepository) =
        SignUpViewModel(
            authRepository = authRepository,
            scope = dependency.navigationStackEntry
        )
}

class SignUpViewModel(
    private val authRepository: AuthRepository,
    scope: CoroutineScopeFactory,
) : ViewModel(
        name = TAG,
        scopeFactory = scope,
    ), SignUpViewListener {

    var username: String = ""
    var password: String = ""

    override fun onClickSignUp() {
        // Login with the provided credentials (fake auth accepts any non-empty values)
        if (username.isNotBlank() && password.isNotBlank()) {
            authRepository.login(username, password)
        } else {
            // For demo purposes, use a default username/password
            authRepository.login("demo@example.com", "password")
        }
        // The MainView will observe auth state and navigate automatically
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }
}
