@file:Suppress("MaximumLineLength")
package com.share.external.lib.mvvm.navigation.content

import androidx.compose.runtime.Composable

fun interface ComposableProvider {
    @Composable
    fun Content()

    @Composable
    fun displayMode(): DisplayMode = DisplayMode.FullScreen
}
