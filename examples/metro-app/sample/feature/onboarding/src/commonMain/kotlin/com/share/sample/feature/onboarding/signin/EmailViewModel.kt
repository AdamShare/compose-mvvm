package com.share.sample.feature.onboarding.signin

import com.getbackcompose.compose.runtime.derivedStateObservingOf
import com.getbackcompose.compose.runtime.mutableStateObservingOf
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.compose.state.ViewModel

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

class EmailViewModel(scope: ManagedCoroutineScope) :
    ViewModel(name = TAG, scopeFactory = scope), SignInEmailTextFieldState, SignInEmailTextFieldListener {
    override var email by mutableStateObservingOf("")
        private set

    override val emailHasErrors by derivedStateObservingOf {
        if (email.isNotEmpty()) {
            !EMAIL_REGEX.matches(email)
        } else {
            false
        }
    }

    override fun onEmailValueChange(value: String) {
        email = value.trim()
    }

    companion object {
        private const val TAG = "EmailViewModel"
    }
}
