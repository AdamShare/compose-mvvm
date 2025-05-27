package com.share.external.lib.mvvm.navigation.modal

import android.view.View
import android.view.View.OnLayoutChangeListener
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import timber.log.Timber

/** Exposes measured layout and inset values from the Android decor view. */
interface DecorViewProperties {
    /** Measured height of the decor view in [Dp]. */
    val height: Dp

    /** Combined system bar insets (status bar, navigation bar) as [PaddingValues]. */
    val systemInsets: PaddingValues

    /** Measured width of the decor view in [Dp]. */
    val width: Dp
}

/**
 * CompositionLocal providing the current [DecorViewProperties].
 *
 * Must be explicitly set using [ComponentActivity.decorViewProperties] at the root of your composition.
 */
val LocalDecorViewProperties = staticCompositionLocalOf<DecorViewProperties> { DecorViewPropertiesNotSet }

/**
 * Composable entry point that observes the current [Window]'s decor view layout and provides [DecorViewProperties]
 * updates on layout changes.
 *
 * Typically used near the root of your app or screen host to enable modal layout features.
 */
@Composable
fun ComponentActivity.decorViewProperties(): DecorViewProperties {
    return window.decorView.decorViewProperties()
}

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

private fun View.decorViewProperties(density: Density) =
    density.run {
        DecorViewPropertiesImpl(
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

/** Internal implementation of [DecorViewProperties] based on actual decor view measurements. */
private data class DecorViewPropertiesImpl(
    override val height: Dp,
    override val systemInsets: PaddingValues,
    override val width: Dp,
) : DecorViewProperties

/**
 * Fallback implementation of [DecorViewProperties] used when the provider is not set.
 *
 * Logs a warning and returns exaggerated default values to reduce risk of rendering content offscreen.
 */
private object DecorViewPropertiesNotSet : DecorViewProperties {
    private const val TAG = "DecorViewPropertiesNotSet"
    override val height: Dp
        get() {
            Timber.tag(TAG).wtf("DecorViewProperties are not set. Modals may have unexpected layouts.")
            return Int.MAX_VALUE.dp
        }

    override val systemInsets: PaddingValues
        get() {
            Timber.tag(TAG).wtf("DecorViewProperties are not set. Modals may have unexpected layouts.")
            return PaddingValues(0.dp)
        }

    override val width: Dp
        get() {
            Timber.tag(TAG).wtf("DecorViewProperties are not set. Modals may have unexpected layouts.")
            return Int.MAX_VALUE.dp
        }
}
