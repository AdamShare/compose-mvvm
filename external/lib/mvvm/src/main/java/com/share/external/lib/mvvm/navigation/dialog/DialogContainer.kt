package com.share.external.lib.mvvm.navigation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun DialogContainer(
    onDismiss: () -> Unit,
    properties: DialogProperties,
    backgroundContent: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)?
) {
    Box {
        backgroundContent?.let {
            Box(modifier = Modifier.zIndex(0f)) {
                it.invoke()
            }
        }

        content?.let { content ->
            Box(
                modifier = Modifier
                    .zIndex(1f)
                    .fillMaxSize()
                    .background(
                        color = Color(
                            red = 0f,
                            green = 0f,
                            blue = 0f,
                            alpha = properties.backgroundAlpha
                        )
                    )
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
                modifier = Modifier
                    .zIndex(2f)
                    .fillMaxSize()
                    .run {
                        if (imeSafeSpace > properties.minimumHeightIMEPaddingEnabled) {
                            imePadding()
                        } else this
                    },
                contentAlignment = properties.contentAlignment,
            ) {
                DialogSurface(
                    properties = properties,
                ) {
                    content.invoke()
                }
            }
        }
    }
}