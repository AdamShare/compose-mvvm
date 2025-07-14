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
import com.share.external.lib.activity.compose.calculateWindowSizeClass
import com.share.external.lib.activity.compose.hasCompactSize
import com.share.external.lib.mvvm.navigation.content.Screen
import com.share.external.lib.mvvm.base.View
import com.share.external.lib.mvvm.navigation.content.ViewPresentation
import com.share.external.lib.compose.modal.ModalProperties
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object SignUpViewModule {
    @SignUpScope @Provides fun screen(viewModel: SignUpViewModel) = SignUpScreen(viewModel = viewModel)
}

class SignUpScreen(private val viewModel: SignUpViewModel) : Screen {
    override fun create(scope: CoroutineScope) = SignUpView(listener = viewModel)

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
