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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.share.external.lib.navigation.stack.NavigationRoute
import com.share.external.lib.navigation.stack.NavigationStack
import com.share.external.lib.navigation.stack.Screen
import com.share.external.lib.navigation.stack.toNavigationRoute
import com.share.external.lib.view.View
import com.share.external.lib.view.ViewProvider
import com.share.sample.feature.onboarding.signin.signup.SignUpComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object SignInViewModule {
    @SignInScope
    @Provides
    fun signInView(
        emailViewModel: EmailViewModel,
        signInViewModel: SignInViewModel,
        dependency: SignInComponent.Dependency,
        signUp: SignUpComponent.Factory
    ) = SignInViewProvider(
        emailViewModel = emailViewModel,
        signInViewModel = signInViewModel,
        navigationStack = dependency.navigationStackEntry,
        signUpRoute = signUp.toNavigationRoute()
    )
}

class SignInViewProvider(
    private val emailViewModel: EmailViewModel,
    private val signInViewModel: SignInViewModel,
    private val navigationStack: NavigationStack<Screen>,
    private val signUpRoute: NavigationRoute<Screen>,
) : Screen {
    override fun onViewAppear(scope: CoroutineScope) = View {
        SignInViewContent(
            emailState = emailViewModel,
            onEmailValueChange = emailViewModel::onEmailValueChange,
            onClickSignIn = signInViewModel::signIn,
            onClickSignUp = { navigationStack.push(signUpRoute) }
        )
    }
}

@Composable
private fun SignInViewContent(
    emailState: SignInEmailTextFieldState,
    onEmailValueChange: (String) -> Unit,
    onClickSignIn: () -> Unit,
    onClickSignUp: () -> Unit
) {
    Column(
        modifier = Modifier.background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(weight = 1f, fill = true), contentAlignment = Alignment.Center) {
            SignInEmailTextField(
                listener = object : SignInEmailTextFieldListener {
                    override fun onEmailValueChange(value: String) = onEmailValueChange(value)
                },
                state = emailState
            )
        }

        Button(modifier = Modifier.fillMaxWidth(), onClick = onClickSignIn) { Text(text = "Sign In") }

        Button(modifier = Modifier.fillMaxWidth(), onClick = onClickSignUp) { Text(text = "Sign Up") }
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
