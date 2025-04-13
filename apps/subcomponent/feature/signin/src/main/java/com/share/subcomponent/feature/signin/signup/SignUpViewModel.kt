package com.share.subcomponent.feature.signin.signup

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.viewmodel.ManagedViewModel

class SignUpViewModel(
    scope: ManagedCoroutineScope,
): ManagedViewModel(TAG, scope), SignUpViewListener {
    override fun onClickSignUp() {

    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }
}
