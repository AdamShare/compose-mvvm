package com.share.external.lib.mvvm.navigation.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
interface NavigationComposableProvider : ComposableProvider, NavigationKey

class NavigationScreen(
    override val analyticsId: String,
    private val displayMode: DisplayMode = DisplayMode.FullScreen,
    private val content: @Composable () -> Unit,
) : NavigationComposableProvider {
    @Composable
    override fun Content() = content()

    @Composable
    override fun displayMode(): DisplayMode = displayMode
}