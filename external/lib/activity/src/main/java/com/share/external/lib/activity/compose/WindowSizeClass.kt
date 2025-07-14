package com.share.external.lib.activity.compose

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun calculateWindowSizeClass(): WindowSizeClass {
    return calculateWindowSizeClass(findActivity())
}

enum class WindowSizeClassMin {
    /** [WindowSizeClass] has [WindowHeightSizeClass.Compact] or [WindowWidthSizeClass.Compact]. */
    Compact,
    /** [WindowSizeClass] has [WindowHeightSizeClass.Medium] or [WindowWidthSizeClass.Medium]. */
    Medium,
    /** [WindowSizeClass] has [WindowHeightSizeClass.Expanded] or [WindowWidthSizeClass.Expanded]. */
    Expanded,
}

/** Width or height is a compact size. */
fun WindowSizeClass.hasCompactSize(): Boolean {
    return min() == WindowSizeClassMin.Compact
}

operator fun WindowSizeClass.compareTo(other: WindowSizeClassMin): Int {
    return min().compareTo(other)
}

operator fun WindowSizeClassMin.compareTo(other: WindowSizeClass): Int {
    return compareTo(other.min())
}

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
