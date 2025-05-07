package com.share.sample.app.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.share.external.lib.mvvm.activity.ActivityComponentInject
import com.share.external.lib.mvvm.activity.ViewModelComponentActivity
import com.share.external.lib.mvvm.navigation.content.ComposableProvider
import com.share.external.lib.mvvm.navigation.dialog.LocalDecorViewProperties
import com.share.external.lib.mvvm.navigation.dialog.decorViewProperties
import com.share.sample.feature.signin.SignInView
import com.share.external.lib.mvvm.viewmodel.getOrCreate
import com.share.sample.feature.signin.SignInComponent

interface SampleActivityComponentInject: ActivityComponentInject<
        SampleActivity,
        SampleActivityComponent,
        SampleActivityComponent.Factory,
        SampleActivityViewModelComponent
        >

class SampleActivity : ViewModelComponentActivity<SampleActivityViewModelComponent>(),
    SampleActivityComponentInject,
    SampleActivityViewModelComponent.Activity
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inject()

        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(
                LocalDecorViewProperties provides decorViewProperties(),
            ) {
                ActivityView(
                     viewModelComponent = viewModelComponent,
                )
            }
        }
    }
}

@Composable
fun ActivityView(
    viewModelComponent: SampleActivityViewModelComponent,
) {
    viewModelComponent.navigationController.Content { route, scope ->
        when (route) {
            is ActivityViewRoute.LoggedIn -> {
                ComposableProvider {
                    Text("Logged in as ${route.user}")

                    Button(onClick = {
                        viewModelComponent.navigationController.selected = ActivityViewRoute.LoggedOut
                    }) {
                        Text("Log Out")
                    }
                }
            }
            ActivityViewRoute.LoggedOut -> {
                viewModelComponent.signInComponentFactory(
                    scope = SignInComponent.Scope(scope)
                ).view
            }
        }
    }
}

