package com.share.external.lib.mvvm.navigation.dialog

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

@Composable
fun DialogSurface(
    modifier: Modifier = Modifier,
    properties: DialogProperties,
    content: @Composable () -> Unit,
) {
    DialogSurface(
        modifier = modifier,
        horizontalPadding = properties.minHorizontalPadding,
        verticalPadding = properties.minVerticalPadding,
        widthRatioRange = properties.widthRatioRange,
        heightRatioRange = properties.heightRatioRange,
        fixedMaxWidth = properties.fixedMaxWidth,
        intrinsicWidth = properties.intrinsicWidth,
        intrinsicHeight = properties.intrinsicHeight,
        usePlatformDefaultWidth = false,
        contentAlignment = properties.contentAlignment,
        content = content,
    )
}

@Composable
fun DialogSurface(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 20.dp,
    verticalPadding: Dp = 20.dp,
    widthRatioRange: ClosedRange<Float> = 0.4f..0.9f,
    heightRatioRange: ClosedRange<Float> = 0.0f..0.9f,
    fixedMaxWidth: Dp? = null,
    intrinsicWidth: Boolean = false,
    intrinsicHeight: Boolean = false,
    contentAlignment: Alignment = Alignment.Center,
    usePlatformDefaultWidth: Boolean = true,
    content: @Composable () -> Unit,
) {
    val decorView = LocalDecorViewProperties.current
    BoxWithConstraints {
        val horizontalBarPadding = decorView.systemInsets.calculateHorizontalPadding()
        val verticalBarPadding = decorView.systemInsets.calculateVerticalPadding()

        val height = decorView.height - verticalBarPadding - verticalPadding
        val width = decorView.width - horizontalBarPadding - horizontalPadding

        val maxBarPadding = maxOf(horizontalBarPadding, verticalBarPadding)
        val maxLength = maxOf(decorView.width - maxBarPadding, decorView.height - maxBarPadding)

        // Use longest length for width to keep width static on orientation change.
        val minLengthIn = Dp(maxLength.value * widthRatioRange.start)
        val maxWidthIn = minOf(fixedMaxWidth ?: maxWidth, width)

        val maxHeightIn = minOf(height, Dp(maxHeight.value * heightRatioRange.endInclusive))
        val minHeightIn = Dp(maxHeight.value * heightRatioRange.start)

        Surface(
            modifier = modifier
                .run {
                    if (contentAlignment != Alignment.Center) {
                        padding(horizontal = horizontalPadding, vertical = verticalPadding)
                    } else this
                }
                .heightIn(
                    min = minOf(minHeightIn, maxHeightIn),
                    max = maxHeightIn,
                )
                .widthIn(
                    min = minOf(minLengthIn, maxWidthIn),
                    max = maxWidthIn,
                )
                .run {
                    if (!usePlatformDefaultWidth && intrinsicWidth) width(IntrinsicSize.Max) else this
                }
                .run {
                    if (!usePlatformDefaultWidth && intrinsicHeight) height(IntrinsicSize.Max) else this
                },
            shape = RoundedCornerShape(16.dp),
        ) {
            content()
        }
    }
}

fun PaddingValues.calculateHorizontalPadding() =
    calculateLeftPadding(LayoutDirection.Ltr) + calculateRightPadding(LayoutDirection.Ltr)

fun PaddingValues.calculateVerticalPadding() = calculateTopPadding() + calculateBottomPadding()