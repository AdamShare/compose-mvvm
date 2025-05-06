package com.share.dynamic.feature.signin.signup

import com.share.external.lib.mvvm.navigation.content.ComposableProvider
import com.share.external.lib.mvvm.navigation.stack.NavigationStackEntry
import com.share.external.lib.mvvm.viewmodel.ManagedViewModel

class SignUpViewModel(
    private val navigationContext: NavigationStackEntry<ComposableProvider>,
): ManagedViewModel(TAG, navigationContext), SignUpViewListener {
    override fun onClickSignUp() {
        navigationContext.remove()
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }
}
