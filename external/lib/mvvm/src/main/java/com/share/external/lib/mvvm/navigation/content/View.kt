@file:Suppress("MaximumLineLength")

package com.share.external.lib.mvvm.navigation.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

/** The composable content to be displayed for this view. */
@Stable
interface View {
    val content: @Composable () -> Unit
}

/** Generic view factory. */
fun View(content: @Composable () -> Unit): View {
    return object : View {
        override val content: @Composable () -> Unit = content
    }
}