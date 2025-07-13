package com.share.sample.feature.onboarding.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.share.external.lib.mvvm.navigation.content.Screen
import com.share.external.lib.mvvm.navigation.content.View
import com.share.external.lib.mvvm.navigation.stack.NavigationStackScope
import com.share.external.lib.mvvm.viewmodel.StateProvider
import com.share.sample.feature.onboarding.signin.signup.SignUpComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import timber.log.Timber

@Module
object SignInViewModule {
    @SignInScope
    @Provides
    fun signInView(emailViewModel: EmailViewModel, scope: SignInComponent.Scope, signUp: SignUpComponent.Factory) =
        SignInViewProvider(emailViewModel = emailViewModel, navigationScope = scope, signUp = signUp)
}

class SignInViewProvider(
    private val emailViewModel: EmailViewModel,
    private val navigationScope: NavigationStackScope<Screen>,
    private val signUp: SignUpComponent.Factory,
) : Screen {
    override fun create(scope: CoroutineScope) = SignInView(
        emailViewModel = emailViewModel,
        navigationScope = navigationScope,
        signUp = signUp,
        scope = scope
    )
}

class SignInView(
    emailViewModel: EmailViewModel,
    private val navigationScope: NavigationStackScope<Screen>,
    private val signUp: SignUpComponent.Factory,
    override val scope: CoroutineScope,
): View,
    SignInViewListener,
    SignInEmailTextFieldListener by emailViewModel,
    SignInEmailTextFieldState by emailViewModel,
    StateProvider {
        init {
            Timber.tag("SignInView").d("init")
        }

    override fun onClickSignIn() {

    }

    override fun onClickSignUp() {
        navigationScope.push(signUp)
    }

    override val content: @Composable () -> Unit = {
        SignInView(listener = this, state = this)
    }
}

interface SignInViewListener : SignInEmailTextFieldListener {
    fun onClickSignIn()

    fun onClickSignUp()
}

@Composable
fun SignInView(listener: SignInViewListener, state: SignInEmailTextFieldState) {
    Column(
        modifier = Modifier.background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(weight = 1f, fill = true), contentAlignment = Alignment.Center) {
            SignInEmailTextField(listener = listener, state = state)
        }

        Button(modifier = Modifier.fillMaxWidth(), onClick = listener::onClickSignIn) { Text(text = "Sign In") }

        Button(modifier = Modifier.fillMaxWidth(), onClick = listener::onClickSignUp) { Text(text = "Sign Up") }
    }
}

interface SignInEmailTextFieldState {
    val email: String
    val emailHasErrors: Boolean
}

interface SignInEmailTextFieldListener {
    fun onEmailValueChange(value: String)
}

@Composable
fun SignInEmailTextField(
    listener: SignInEmailTextFieldListener,
    state: SignInEmailTextFieldState,
    modifier: Modifier = Modifier,
) {
    TextField(
        modifier = modifier,
        value = state.email,
        isError = state.emailHasErrors,
        onValueChange = listener::onEmailValueChange,
        label = { Text(text = "Email") },
    )
}

class UserPreviewParameterProvider : PreviewParameterProvider<UserPreviewParameterProvider.Handler> {
    override val values = sequenceOf(Handler("testemail@gmail.com", false), Handler("testemail@!", true))

    data class Handler(override val email: String = "testemail", override val emailHasErrors: Boolean = false) :
        SignInViewListener, SignInEmailTextFieldState {
        override fun onClickSignIn() {}

        override fun onClickSignUp() {}

        override fun onEmailValueChange(value: String) {}
    }
}

@Preview
@Composable
fun SignInViewPreview(
    @PreviewParameter(UserPreviewParameterProvider::class) handler: UserPreviewParameterProvider.Handler
) {
    SignInView(listener = handler, state = handler)
}
