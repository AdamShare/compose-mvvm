package com.share.external.lib.activity.compose

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Finds the Activity of type [T] from this Context.
 *
 * Traverses the Context hierarchy (through [ContextWrapper]s) to find the hosting Activity.
 * The result is remembered across recompositions.
 *
 * @param T The expected Activity type.
 * @return The Activity of type [T].
 * @throws IllegalStateException if no Activity of type [T] is found in the Context hierarchy.
 */
@Composable
inline fun <reified T : Activity> Context.findActivity(): T {
    return remember {
        var context = this
        while (context is ContextWrapper) {
            if (context is T) return@remember context
            context = context.baseContext
        }
        error("no activity of type ${T::class}")
    }
}

/**
 * Finds the Activity of type [T] from [LocalContext].
 *
 * Convenience function that uses the current composition's Context.
 *
 * @param T The expected Activity type.
 * @return The Activity of type [T].
 * @throws IllegalStateException if no Activity of type [T] is found.
 */
@Composable
inline fun <reified T : Activity> findActivity(): T {
    return LocalContext.current.findActivity()
}
