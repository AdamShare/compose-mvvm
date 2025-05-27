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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.share.external.lib.mvvm.activity.calculateWindowSizeClass
import com.share.external.lib.mvvm.activity.hasCompactSize
import com.share.external.lib.mvvm.navigation.content.Screen
import com.share.external.lib.mvvm.navigation.content.ViewPresentation
import com.share.external.lib.mvvm.navigation.modal.ModalProperties
import dagger.Module
import dagger.Provides

@Module
object SignUpViewModule {
    @SignUpScope @Provides fun view(viewModel: SignUpViewModel) = SignUpView(viewModel = viewModel)
}

class SignUpView(private val viewModel: SignUpViewModel) : Screen {
    override val content: @Composable () -> Unit = { SignUpView(listener = viewModel) }

    @Composable
    override fun preferredPresentationStyle(): ViewPresentation.Style {
        return if (calculateWindowSizeClass().hasCompactSize()) {
            ViewPresentation.Style.FullScreen
        } else {
            ViewPresentation.Style.Modal(properties = ModalProperties(intrinsicHeight = true, intrinsicWidth = true))
        }
    }
}

interface SignUpViewListener {
    fun onClickSignUp()
}

@Composable
fun SignUpView(listener: SignUpViewListener) {
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

object SignUpViewListenerPreview : SignUpViewListener {
    override fun onClickSignUp() {}
}

@Preview
@Composable
fun SignUpViewPreview() {
    SignUpView(listener = SignUpViewListenerPreview)
}
