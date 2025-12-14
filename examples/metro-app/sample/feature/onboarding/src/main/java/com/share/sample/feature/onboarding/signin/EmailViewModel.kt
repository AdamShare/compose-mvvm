package com.share.sample.feature.onboarding.signin

import android.util.Patterns
import com.getbackcompose.compose.runtime.derivedStateObservingOf
import com.getbackcompose.compose.runtime.mutableStateObservingOf
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.compose.state.ViewModel

class EmailViewModel(scope: ManagedCoroutineScope) :
    ViewModel(name = TAG, scopeFactory = scope), SignInEmailTextFieldState, SignInEmailTextFieldListener {
    override var email by mutableStateObservingOf("")
        private set

    override val emailHasErrors by derivedStateObservingOf {
        if (email.isNotEmpty()) {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
