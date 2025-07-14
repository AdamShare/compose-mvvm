package com.share.external.lib.mvvm.navigation.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.share.compose.foundation.layout.LocalDecorViewProperties

/**
 * Root container for presenting modal content with background scrim, keyboard insets, and dismiss handling.
 *
 * This composable is responsible for rendering the modal's layered structure, including:
 * - An optional background composable beneath the modal.
 * - A scrim (semi-transparent overlay) behind the modal content.
 * - A foreground modal surface aligned and sized according to [ModalProperties].
 *
 * ### Features
 * - Handles dismiss behavior when tapping outside the modal, based on [ModalProperties.dismissOnClickOutside].
 * - Applies a scrim with configurable transparency via [ModalProperties.scrimAlpha].
 * - Supports optional resizing when the on-screen keyboard (IME) is shown, gated by
 *   [ModalProperties.imeResizingMinHeightThreshold].
 * - Aligns modal content using [ModalProperties.contentAlignment].
 *
 * @param onDismiss Called when the modal should be dismissed (e.g. scrim tap).
 * @param properties Layout, sizing, and behavior configuration for the modal.
 * @param content Foreground modal content to be displayed in a surface.
 */
@Composable
fun ModalContainer(onDismiss: () -> Unit, properties: ModalProperties, content: @Composable () -> Unit) {
    Box(
        modifier =
            Modifier.zIndex(1f)
                .fillMaxSize()
                .background(color = Color(red = 0f, green = 0f, blue = 0f, alpha = properties.scrimAlpha))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = properties.dismissOnClickOutside,
                    onClick = onDismiss,
                )
    )

    val imeBottomInset = WindowInsets.ime.getBottom(LocalDensity.current)
    val imeSafeSpace = LocalDecorViewProperties.current.height - imeBottomInset.dp
    Box(
        modifier =
            Modifier.zIndex(2f).fillMaxSize().run {
                if (imeSafeSpace > properties.imeResizingMinHeightThreshold) {
                    imePadding()
                } else this
            },
        contentAlignment = properties.contentAlignment,
    ) {
        ModalSurface(properties = properties) { content.invoke() }
    }
}
