package com.share.external.lib.mvvm.navigation.modal

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

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
fun ModalSurface(modifier: Modifier = Modifier, properties: ModalProperties, content: @Composable () -> Unit) {
    val decorView = LocalDecorViewProperties.current
    BoxWithConstraints {
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

        Surface(
            modifier =
                modifier
                    .run {
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
                    .run { if (properties.intrinsicHeight) height(IntrinsicSize.Max) else this },
            shape = RoundedCornerShape(16.dp),
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
