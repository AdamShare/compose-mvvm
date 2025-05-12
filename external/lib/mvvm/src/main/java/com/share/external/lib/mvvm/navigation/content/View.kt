@file:Suppress("MaximumLineLength")
package com.share.external.lib.mvvm.navigation.content

import androidx.compose.runtime.Composable
import com.share.external.lib.mvvm.navigation.dialog.DialogProperties

fun interface View {
    @Composable
    fun Content()

    @Composable
    fun preferredPresentation(): Presentation = Presentation.FullScreen
}

class OverlayView(
    dialogProperties: DialogProperties = DialogProperties(),
    private val content: @Composable () -> Unit,
): View {
    private val presentation = Presentation.Overlay(dialogProperties)
    @Composable
    override fun preferredPresentation(): Presentation = presentation

    @Composable
    override fun Content() = content()
}