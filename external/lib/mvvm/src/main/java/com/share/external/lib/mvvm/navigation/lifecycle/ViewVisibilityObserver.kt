package com.share.external.lib.mvvm.navigation.lifecycle

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.share.external.lib.mvvm.activity.findActivity

@SuppressLint("RememberReturnType")
@Composable
fun ViewVisibilityObserver(
    onVisible: () -> Unit,
    onHidden: () -> Unit,
) {
    val activity = findActivity<Activity>()
    remember {
        // Run on initial composition as DisposableEffect is delayed.
        onVisible()
    }

    // Track when the view is composed and disposed
    DisposableEffect(Unit) {
        onDispose {
            if (!activity.isChangingConfigurations) {
                onHidden()
            }
        }
    }

    // Track lifecycle transitions (resume/pause)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> onVisible()
                Lifecycle.Event.ON_PAUSE -> if (!activity.isChangingConfigurations) {
                    onHidden()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
