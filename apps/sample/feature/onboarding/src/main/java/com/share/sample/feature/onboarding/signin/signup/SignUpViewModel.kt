package com.share.sample.feature.onboarding.signin.signup

import com.share.external.lib.mvvm.navigation.content.Screen
import com.share.external.lib.mvvm.navigation.stack.NavigationStackEntry
import com.share.external.lib.mvvm.viewmodel.ViewLifecycleViewModel
import com.share.external.lib.mvvm.viewmodel.ViewModel

class SignUpViewModel(private val navigationContext: NavigationStackEntry<Screen>) :
    ViewLifecycleViewModel(
        name = TAG,
        scope = navigationContext,
    ), SignUpViewListener {
    override fun onClickSignUp() {
        navigationContext.remove()
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }
}
