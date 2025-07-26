@file:Suppress("MaximumLineLength")

package com.share.external.lib.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
interface View {
    /** The composable content to be displayed for this view. */
    val content: @Composable () -> Unit
}

/** Generic view factory. */
fun View(content: @Composable () -> Unit): View {
    return object : View {
        override val content: @Composable () -> Unit = content
    }
}