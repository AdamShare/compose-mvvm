package com.share.subcomponent.app.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.share.external.lib.mvvm.activity.ActivityComponentInject
import com.share.external.lib.mvvm.activity.ViewModelComponentActivity
import com.share.external.lib.mvvm.navigation.dialog.LocalDecorViewProperties
import com.share.external.lib.mvvm.navigation.dialog.decorViewProperties
import com.share.subcomponent.feature.signin.SignInView

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
    SignInView(
        viewModelComponent.signInComponentFactory
    )
}
