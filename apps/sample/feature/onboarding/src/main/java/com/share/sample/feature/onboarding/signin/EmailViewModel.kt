package com.share.sample.feature.onboarding.signin

import android.util.Patterns
import com.share.external.lib.compose.runtime.derivedStateObservingOf
import com.share.external.lib.compose.runtime.mutableStateObservingOf
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.compose.state.ViewModel

class EmailViewModel(scope: ManagedCoroutineScope) :
    ViewModel(name = TAG, scope = scope), SignInEmailTextFieldState, SignInEmailTextFieldListener {
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
