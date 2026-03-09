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
import androidx.compose.ui.unit.dp
import com.getbackcompose.navigation.stack.NavigationRoute
import com.getbackcompose.navigation.stack.NavigationStack
import com.getbackcompose.navigation.stack.Screen
import com.getbackcompose.core.View
import com.share.sample.core.auth.AuthRepository
import com.share.sample.feature.onboarding.signin.signup.SignUpScreen
import kotlinx.coroutines.CoroutineScope

/**
 * Routes for the sign-in navigation.
 */
enum class SignInRoute : com.getbackcompose.core.ViewKey {
    SignUp
}

class SignInViewProvider(
    private val authRepository: AuthRepository,
    private val navigationStack: NavigationStack<Screen>,
    private val managedScope: com.getbackcompose.foundation.coroutines.ManagedCoroutineScope,
) : Screen {
    override fun onViewAppear(scope: CoroutineScope): View {
        val emailViewModel = EmailViewModel(scope = managedScope)
        val signInViewModel = SignInViewModel(
            emailViewModel = emailViewModel,
            authRepository = authRepository
        )

        return View {
            SignInViewContent(
                emailState = emailViewModel,
                onEmailValueChange = emailViewModel::onEmailValueChange,
                onClickSignIn = signInViewModel::signIn,
                onClickSignUp = {
                    navigationStack.push(
                        NavigationRoute(
                            key = SignInRoute.SignUp,
                            factory = { navScope ->
                                SignUpScreen(
                                    navigationStack = navScope,
                                    authRepository = authRepository
                                )
                            }
                        )
                    )
                }
            )
        }
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
