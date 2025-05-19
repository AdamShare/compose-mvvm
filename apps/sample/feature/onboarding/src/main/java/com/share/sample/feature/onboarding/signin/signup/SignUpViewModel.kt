package com.share.sample.feature.signin.signup

import com.share.external.lib.mvvm.navigation.content.View
import com.share.external.lib.mvvm.navigation.stack.NavigationStackEntry
import com.share.external.lib.mvvm.viewmodel.ViewModel

class SignUpViewModel(
    private val navigationContext: NavigationStackEntry<View>,
): ViewModel(TAG, navigationContext), SignUpViewListener {
    override fun onClickSignUp() {
        navigationContext.remove()
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }
}
