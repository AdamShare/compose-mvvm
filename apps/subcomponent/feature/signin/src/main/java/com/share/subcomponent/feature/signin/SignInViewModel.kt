package com.share.subcomponent.feature.signin

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.v1.push
import com.share.external.lib.mvvm.viewmodel.ManagedViewModel
import com.share.subcomponent.feature.signin.signup.SignUpComponent

class SignInViewModel(
    private val signUp: SignUpComponent.Factory,
    val navigationController: SignInNavigationController,
    scope: ManagedCoroutineScope,
): ManagedViewModel(TAG, scope), SignInViewListener {
    override fun onClickSignIn() {
    }

    override fun onClickSignUp() {
        navigationController.push(signUp().view)
    }

    companion object {
        private const val TAG = "SignInViewModel"
    }
}
