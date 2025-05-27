@file:Suppress("MaximumLineLength")

package com.share.external.lib.mvvm.navigation.content

import androidx.compose.runtime.Composable

/** The composable content to be displayed for this view. */
interface View {
    val content: @Composable () -> Unit
}
