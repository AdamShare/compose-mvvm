package com.share.external.lib.activity.compose

import android.app.Activity
import android.view.View
import android.view.View.OnLayoutChangeListener
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.share.external.lib.compose.foundation.layout.DecorViewProperties

/**
 * Composable entry point that observes the current [android.view.Window]'s decor view layout and
 * provides [DecorViewProperties] updates on layout changes.
 *
 * Typically used near the root of your app or screen host to enable modal layout features.
 */
@Composable
fun Activity.decorViewProperties(): DecorViewProperties {
    return window.decorView.decorViewProperties()
}

/** Internal implementation of [DecorViewProperties] based on actual decor view measurements. */
private data class ActivityDecorViewProperties(
    override val height: Dp,
    override val systemInsets: PaddingValues,
    override val width: Dp,
) : DecorViewProperties

/**
 * Observes layout and system insets from the given [decorView], and updates [DecorViewProperties] accordingly when
 * layout changes occur (e.g., rotation, keyboard, resizes).
 */
@Composable
private fun View.decorViewProperties(): DecorViewProperties {
    val density = LocalDensity.current
    var properties by remember { mutableStateOf(decorViewProperties(density)) }

    DisposableEffect(this, density) {
        val layoutChangeListener = OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            properties = decorViewProperties(density)
        }
        addOnLayoutChangeListener(layoutChangeListener)
        onDispose { removeOnLayoutChangeListener(layoutChangeListener) }
    }

    return properties
}

private fun View.decorViewProperties(density: Density) = density.run {
    ActivityDecorViewProperties(
            height = height.toDp(),
            systemInsets = rootWindowSystemWindowInsets(density = density),
            width = width.toDp(),
        )
    }

/**
 * Calculates [PaddingValues] for the system bars (status and navigation) using root window insets.
 *
 * @return Padding corresponding to system bar dimensions in [Dp].
 */
private fun View.rootWindowSystemWindowInsets(density: Density): PaddingValues {
    return ViewCompat.getRootWindowInsets(this)?.getInsets(WindowInsetsCompat.Type.systemBars())?.let { insets ->
        density.run {
            PaddingValues(
                start = insets.left.toDp(),
                top = insets.top.toDp(),
                end = insets.right.toDp(),
                bottom = insets.bottom.toDp(),
            )
        }
    } ?: PaddingValues(Dp(0f))
}
