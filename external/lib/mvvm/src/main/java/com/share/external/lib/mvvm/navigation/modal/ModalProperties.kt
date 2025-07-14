package com.share.external.lib.mvvm.navigation.modal

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration options for modal layout, behavior, and appearance.
 *
 * This class defines how a modal should behave when presented in a Compose UI environment. It includes options for
 * dismissal behavior, layout constraints, alignment, and appearance.
 *
 * ### Behavior
 * - Modals can be dismissed by back press or by tapping outside the content (scrim).
 * - You can control whether the modal resizes when the keyboard (IME) is shown.
 * - A minimum size class can be specified to fall back to full-screen mode on smaller devices.
 *
 * ### Layout
 * - Modals are constrained by size ratios and minimum padding relative to the parent container.
 * - Intrinsic sizing can be enabled for width and/or height within the defined ratio ranges.
 * - An optional hard maximum width can override size calculations.
 *
 * ### Appearance
 * - Modals can be aligned within their parent using [contentAlignment].
 * - A dimmed scrim background is shown behind the modal, with customizable opacity.
 */
@Immutable
data class ModalProperties(
    /** Whether tapping outside the modal (on the scrim) should dismiss it. */
    val dismissOnClickOutside: Boolean = true,

    /**
     * Minimum horizontal padding between the modal and the edge of the parent container. This constraint takes
     * precedence over size ratio calculations.
     */
    val minHorizontalPadding: Dp = 40.dp,

    /**
     * Minimum vertical padding between the modal and the edge of the parent container. This constraint takes precedence
     * over size ratio calculations.
     */
    val minVerticalPadding: Dp = 40.dp,

    /** Range (as a fraction of parent width) that the modal’s width can occupy. */
    val widthRatioRange: ClosedRange<Float> = 0.0f..1.0f,

    /** Range (as a fraction of parent height) that the modal’s height can occupy. */
    val heightRatioRange: ClosedRange<Float> = 0.0f..1.0f,

    /** Optional hard limit on the modal’s width, applied after ratio and padding constraints. */
    val maxWidthOverride: Dp? = null,

    /** Whether the modal should use its intrinsic width within the allowed ratio range. */
    val intrinsicWidth: Boolean = false,

    /** Whether the modal should use its intrinsic height within the allowed ratio range. */
    val intrinsicHeight: Boolean = false,

    /**
     * Minimum available height required to enable modal resizing when the keyboard (IME) appears. If unavailable,
     * default keyboard behavior applies without resizing.
     */
    val imeResizingMinHeightThreshold: Dp = 400.dp,

    /** Alignment of the modal content within its parent container. */
    val contentAlignment: Alignment = Alignment.Center,

    /** Opacity of the scrim (background dimming layer) behind the modal. */
    val scrimAlpha: Float = 0.20f,
)
