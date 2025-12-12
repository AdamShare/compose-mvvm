package com.share.external.lib.activity.compose

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

/**
 * Calculates the [WindowSizeClass] for the current Activity.
 *
 * Uses [findActivity] to locate the hosting Activity and calculate its window size class.
 *
 * @return The current [WindowSizeClass] based on window dimensions.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun calculateWindowSizeClass(): WindowSizeClass {
    return calculateWindowSizeClass(findActivity())
}

/**
 * Simplified window size classification based on the minimum of width and height.
 *
 * This enum helps determine responsive layouts by checking the smaller dimension,
 * which is useful for ensuring content fits regardless of orientation.
 */
enum class WindowSizeClassMin {
    /** [WindowSizeClass] has [WindowHeightSizeClass.Compact] or [WindowWidthSizeClass.Compact]. */
    Compact,
    /** [WindowSizeClass] has [WindowHeightSizeClass.Medium] or [WindowWidthSizeClass.Medium]. */
    Medium,
    /** [WindowSizeClass] has [WindowHeightSizeClass.Expanded] or [WindowWidthSizeClass.Expanded]. */
    Expanded,
}

/**
 * Returns `true` if either width or height is compact size.
 *
 * Useful for determining if the screen is too small for certain layouts.
 */
fun WindowSizeClass.hasCompactSize(): Boolean {
    return min() == WindowSizeClassMin.Compact
}

/**
 * Compares this [WindowSizeClass] to a [WindowSizeClassMin].
 *
 * Uses the minimum of width/height for comparison.
 */
operator fun WindowSizeClass.compareTo(other: WindowSizeClassMin): Int {
    return min().compareTo(other)
}

/**
 * Compares this [WindowSizeClassMin] to a [WindowSizeClass].
 */
operator fun WindowSizeClassMin.compareTo(other: WindowSizeClass): Int {
    return compareTo(other.min())
}

/**
 * Returns the minimum size class between width and height.
 *
 * This is useful for responsive layouts that need to adapt to the constraining dimension.
 */
fun WindowSizeClass.min(): WindowSizeClassMin {
    return minOf(heightSizeClass.toWindowSizeClassMin(), widthSizeClass.toWindowSizeClassMin())
}

private fun WindowHeightSizeClass.toWindowSizeClassMin(): WindowSizeClassMin {
    return when (this) {
        WindowHeightSizeClass.Compact -> WindowSizeClassMin.Compact
        WindowHeightSizeClass.Medium -> WindowSizeClassMin.Medium
        WindowHeightSizeClass.Expanded -> WindowSizeClassMin.Expanded
        else -> WindowSizeClassMin.Compact
    }
}

private fun WindowWidthSizeClass.toWindowSizeClassMin(): WindowSizeClassMin {
    return when (this) {
        WindowWidthSizeClass.Compact -> WindowSizeClassMin.Compact
        WindowWidthSizeClass.Medium -> WindowSizeClassMin.Medium
        WindowWidthSizeClass.Expanded -> WindowSizeClassMin.Expanded
        else -> WindowSizeClassMin.Compact
    }
}
