package com.share.external.lib.mvvm.activity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
inline fun <reified T : Activity> Context.findActivity(): T {
    return remember {
        var context = this
        while (context is ContextWrapper) {
            if (context is T) return@remember context
            context = context.baseContext
        }
        throw IllegalStateException("no activity of type ${T::class}")
    }
}

@Composable
inline fun <reified T : Activity> findActivity(): T {
    return LocalContext.current.findActivity()
}
