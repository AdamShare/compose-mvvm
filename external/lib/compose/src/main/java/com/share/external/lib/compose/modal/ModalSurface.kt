package com.share.external.lib.compose.modal

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.share.external.lib.compose.foundation.layout.LocalDecorViewProperties

/**
 * Core composable that renders a modal surface constrained by [ModalProperties].
 *
 * This modal surface adapts to available screen space using ratio-based sizing and inset-aware padding, while
 * optionally supporting intrinsic sizing and hard width limits.
 *
 * ### Behavior
 * - Accounts for system insets using [LocalDecorViewProperties] to avoid overlap with system bars.
 * - Applies minimum horizontal/vertical padding via [properties.minHorizontalPadding] and
 *   [properties.minVerticalPadding].
 * - Sizes the modal between [properties.widthRatioRange] and [properties.heightRatioRange], using the longest screen
 *   dimension to stabilize width across orientation changes.
 * - Applies intrinsic width/height sizing if [properties.intrinsicWidth] or [properties.intrinsicHeight] is enabled.
 * - Applies a rounded corner surface shape by default.
 *
 * @param modifier Modifier to apply to the modal surface.
 * @param properties Configuration for layout, sizing, and behavior.
 * @param content Composable content displayed inside the modal.
 */
@Composable
fun ModalSurface(modifier: Modifier = Modifier, properties: ModalProperties?, content: @Composable () -> Unit) {
    val decorView = LocalDecorViewProperties.current

    BoxWithConstraints(
        modifier = properties?.let {
            val imeBottomInset = WindowInsets.ime.getBottom(LocalDensity.current)
            val imeSafeSpace = decorView.height - imeBottomInset.dp

            modifier.zIndex(2f).fillMaxSize().run {
                if (imeSafeSpace > properties.imeResizingMinHeightThreshold) {
                    imePadding()
                } else this
            }
        } ?: modifier,
        contentAlignment = properties?.contentAlignment ?: Alignment.Center,
    ) {
        Surface(
            modifier = properties?.let {
                val horizontalBarPadding = decorView.systemInsets.calculateHorizontalPadding()
                val verticalBarPadding = decorView.systemInsets.calculateVerticalPadding()

                val height = decorView.height - verticalBarPadding - properties.minVerticalPadding
                val width = decorView.width - horizontalBarPadding - properties.minHorizontalPadding

                val maxBarPadding = maxOf(horizontalBarPadding, verticalBarPadding)
                val maxLength = maxOf(decorView.width - maxBarPadding, decorView.height - maxBarPadding)

                // Use longest length for width to keep width static on orientation change.
                val minLengthIn = Dp(maxLength.value * properties.widthRatioRange.start)
                val maxWidthIn = minOf(properties.maxWidthOverride ?: maxWidth, width)

                val maxHeightIn = minOf(height, Dp(maxHeight.value * properties.heightRatioRange.endInclusive))
                val minHeightIn = Dp(maxHeight.value * properties.heightRatioRange.start)

                Modifier.run {
                        if (properties.contentAlignment != Alignment.Center) {
                            padding(
                                horizontal = properties.minHorizontalPadding,
                                vertical = properties.minVerticalPadding,
                            )
                        } else this
                    }
                    .heightIn(min = minOf(minHeightIn, maxHeightIn), max = maxHeightIn)
                    .widthIn(min = minOf(minLengthIn, maxWidthIn), max = maxWidthIn)
                    .run { if (properties.intrinsicWidth) width(IntrinsicSize.Max) else this }
                    .run { if (properties.intrinsicHeight) height(IntrinsicSize.Max) else this }
            } ?: Modifier,
            shape = properties?.shape ?: RectangleShape,
        ) {
            content()
        }
    }
}

/** Calculates total horizontal padding (left + right) from [PaddingValues]. */
fun PaddingValues.calculateHorizontalPadding() =
    calculateLeftPadding(LayoutDirection.Ltr) + calculateRightPadding(LayoutDirection.Ltr)

/** Calculates total vertical padding (top + bottom) from [PaddingValues]. */
fun PaddingValues.calculateVerticalPadding() = calculateTopPadding() + calculateBottomPadding()
