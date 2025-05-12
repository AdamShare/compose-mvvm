package com.share.sample.feature.signin

import com.css.android.compose.runtime.derivedStateObservingOf
import com.css.android.compose.runtime.mutableStateObservingOf
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.viewmodel.ManagedViewModel

class EmailViewModel(
    scope: ManagedCoroutineScope,
): ManagedViewModel(
    name = TAG,
    scope = scope
), SignInEmailTextFieldState, SignInEmailTextFieldListener {
    override var email by mutableStateObservingOf("")
        private set

    override val emailHasErrors by derivedStateObservingOf {
        if (email.isNotEmpty()) {
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
