package com.share.dynamic.feature.signin

import com.share.external.lib.mvvm.viewmodel.ManagedViewModel
import com.share.dynamic.feature.signin.signup.SignUpComponent
import com.share.dynamic.feature.signin.signup.SignUpNavigationStackEntry
import com.share.external.lib.mvvm.navigation.content.ComposableProvider
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.stack.RootNavigationContext

class SignInViewModel(
    private val navigationContext: RootNavigationContext<ComposableProvider>,
    private val signUp: SignUpComponent.Factory,
): ManagedViewModel(TAG, navigationContext), SignInViewListener {
    override fun onClickSignIn() {
    }

    override fun onClickSignUp() {
        navigationContext.push(SignInRoute.SIGN_UP) {
            signUp(SignUpNavigationStackEntry(it)).view
        }
    }

    companion object {
        private const val TAG = "SignInViewModel"
    }
}

enum class SignInRoute(
    override val analyticsId: String
): NavigationKey {
    SIGN_UP("SignUp")
}