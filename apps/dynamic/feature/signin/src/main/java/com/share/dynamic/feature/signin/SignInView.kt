package com.share.dynamic.feature.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.share.dynamic.feature.signin.signup.SignUpView
import com.share.dynamic.feature.signin.signup.SignUpViewModel
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.activity.calculateWindowSizeClass
import com.share.external.lib.mvvm.activity.hasCompactSize
import com.share.external.lib.mvvm.navigation.content.ComposableProvider
import com.share.external.lib.mvvm.navigation.content.DisplayMode
import com.share.external.lib.mvvm.navigation.dialog.DialogProperties
import com.share.external.lib.mvvm.navigation.stack.NavigationStackController
import com.share.external.lib.mvvm.viewmodel.viewModel

class SignInView(
    private val navigationController: NavigationStackController<ComposableProvider>,
    private val viewModel: SignInViewModel,
): ComposableProvider {
    @Composable
    override fun Content() {
        navigationController.Content {
            SignInView(
                listener = viewModel,
            )
        }
    }
}

interface SignInViewListener {
    fun onClickSignIn()
    fun onClickSignUp()
}

@Composable
fun SignInView(
    listener: SignInViewListener,
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, true),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                textAlign = TextAlign.Center,
                text ="Welcome"
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = listener::onClickSignIn
        ) {
            Text("Sign In")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = listener::onClickSignUp
        ) {
            Text("Sign Up")
        }
    }
}

object SignInViewListenerPreview: SignInViewListener {
    override fun onClickSignIn() {  }
    override fun onClickSignUp() {  }
}

@Preview
@Composable
fun SignInViewPreview() {
    SignInView(
        listener = SignInViewListenerPreview,
    )
}

