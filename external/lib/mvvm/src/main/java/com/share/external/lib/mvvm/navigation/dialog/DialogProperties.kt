package com.share.external.lib.mvvm.navigation.dialog

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.share.external.lib.mvvm.activity.WindowSizeClassMin

@Immutable
data class DialogProperties(
    /**
     * Back handler will pop dialog stack
     */
    val dismissOnBackPress: Boolean = true,
    /**
     * Click outside dialog on "scrim" will dismiss dialog
     */
    val dismissOnClickOutside: Boolean = true,
    /**
     * Minimum horizontal padding from edge of containing view
     * Has priority over all other calculations to not exceed this padding when calculating size.
     */
    val minHorizontalPadding: Dp = 40.dp,
    /**
     * Minimum vertical padding from edge of containing view.
     * Has priority over all other calculations to not exceed this padding when calculating size.
     */
    val minVerticalPadding: Dp = 40.dp,
    /**
     * Minimum and maximum width that the dialog will size inside in relation to the containing view.
     */
    val widthRatioRange: ClosedRange<Float> = 0.4f..1.0f,
    /**
     * Minimum and maximum height that the dialog will size inside in relation to the containing view.
     */
    val heightRatioRange: ClosedRange<Float> = 0.0f..1.0f,
    /**
     * Optionally set a maximum width that will not be exceeded after all other calculations.
     */
    val fixedMaxWidth: Dp? = null,
    /**
     * If the width should attempt to size intrinsically between the specified ranges.
     */
    val intrinsicWidth: Boolean = false,
    /**
     * If the height should attempt to size intrinsically between the specified ranges.
     */
    val intrinsicHeight: Boolean = false,
    /**
     * Minimum height that must be available to apply IME padding that resizes the height to fit the keyboard.
     * Otherwise the standard keyboard focusing will apply with no resizing of the dialog
     */
    val minimumHeightIMEPaddingEnabled: Dp = 400.dp,
    /**
     * Minimum size to show as a dialog. Otherwise will be presented to fill max size.
     */
    val minimumSizeClass: WindowSizeClassMin = WindowSizeClassMin.Compact,
    /**
     * Content alignment of the dialog inside the parent view.
     */
    val contentAlignment: Alignment = Alignment.Center,
    /**
     * Background color alpha of the dialog.
     */
    val backgroundAlpha: Float = 0.20f,
)