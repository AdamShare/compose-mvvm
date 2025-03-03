package com.share.subcomponent.feature.signin

import androidx.lifecycle.ViewModel
import com.share.external.lib.mvvm.navigation.ComposableProvider
import com.share.external.lib.mvvm.navigation.stack.NavigationStack

class SignInViewModel(
    private val navigationStack: NavigationStack<Any, ComposableProvider>,
): ViewModel(), SignInViewListener {
    override fun onClickSignIn() {

    }

    override fun onClickSignUp() {
//        navigationStack.push(ComposableProvider {
//
//        })
    }
}

interface SignInViewListener {
    fun onClickSignIn()
    fun onClickSignUp()
}