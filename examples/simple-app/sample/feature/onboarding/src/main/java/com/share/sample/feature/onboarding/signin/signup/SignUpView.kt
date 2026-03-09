package com.share.sample.feature.onboarding.signin.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.getbackcompose.activity.compose.calculateWindowSizeClass
import com.getbackcompose.activity.compose.hasCompactSize
import com.getbackcompose.navigation.stack.Screen
import com.getbackcompose.core.View
import com.getbackcompose.core.ViewPresentation
import com.getbackcompose.compose.modal.ModalProperties
import kotlinx.coroutines.CoroutineScope

class SignUpScreen(
    private val navigationStack: com.getbackcompose.navigation.stack.NavigationStack<Screen>,
    private val authRepository: com.share.sample.core.auth.AuthRepository
) : Screen {
    override fun onViewAppear(scope: CoroutineScope): View {
        val viewModel = SignUpViewModel(
            authRepository = authRepository,
            scope = navigationStack as com.getbackcompose.navigation.stack.NavigationStackEntry<Screen>
        )
        return SignUpView(listener = viewModel)
    }

    override val preferredPresentationStyle get() = @Composable {
        val compact = calculateWindowSizeClass().hasCompactSize()
        remember(compact) {
            if (compact) {
                ViewPresentation.Style.FullScreen
            } else {
                ViewPresentation.Style.Modal(
                    properties = ModalProperties(
                        intrinsicHeight = true,
                        intrinsicWidth = true
                    )
                )
            }
        }
    }
}

interface SignUpViewListener {
    fun onClickSignUp()
}

class SignUpView(
    listener: SignUpViewListener,
): View {
    override val content: @Composable () -> Unit = {
        Column(
            modifier = Modifier.background(Color.White).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f, true), contentAlignment = Alignment.Center) {
                Text(textAlign = TextAlign.Center, text = "Create a new account")
            }

            Button(modifier = Modifier.fillMaxWidth(), onClick = listener::onClickSignUp) { Text("Sign Up") }
        }
    }
}

object SignUpViewListenerPreview : SignUpViewListener {
    override fun onClickSignUp() {}
}

@Preview
@Composable
fun SignUpViewPreview() {
    SignUpView(listener = SignUpViewListenerPreview).content()
}
