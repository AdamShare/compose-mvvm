package com.share.external.lib.mvvm.navigation.dialog

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

interface DecorViewProperties {
    val height: Dp
    val systemInsets: PaddingValues
    val width: Dp
}

private data class DecorViewPropertiesImpl(
    override val height: Dp,
    override val systemInsets: PaddingValues,
    override val width: Dp,
) : DecorViewProperties

object DecorViewPropertiesNotSet : DecorViewProperties {
    private const val TAG = "DecorViewPropertiesNotSet"
    override val height: Dp
        get() {
            Timber.tag(TAG).wtf("DecorViewProperties are not set. Dialogs won't work as expected.")
            return Int.MAX_VALUE.dp
        }

    override val systemInsets: PaddingValues
        get() {
            Timber.tag(TAG).wtf("DecorViewProperties are not set. Dialogs won't work as expected.")
            return PaddingValues(0.dp)
        }

    override val width: Dp
        get() {
            Timber.tag(TAG).wtf("DecorViewProperties are not set. Dialogs won't work as expected.")
            return Int.MAX_VALUE.dp
        }
}

val LocalDecorViewProperties = staticCompositionLocalOf<DecorViewProperties> {
    DecorViewPropertiesNotSet
}

@Composable
fun ComponentActivity.decorViewProperties(): DecorViewProperties {
    return decorViewProperties(window.decorView)
}

@Composable
private fun decorViewProperties(decorView: View): DecorViewProperties {
    val density = LocalDensity.current
    var properties by remember {
        mutableStateOf(
            density.run {
                DecorViewPropertiesImpl(
                    height = decorView.height.toDp(),
                    systemInsets = decorView.rootWindowSystemWindowInsets(this),
                    width = decorView.width.toDp(),
                )
            }
        )
    }

    DisposableEffect(decorView, density) {
        val layoutChangeListener = OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            properties = density.run {
                DecorViewPropertiesImpl(
                    height = decorView.height.toDp(),
                    systemInsets = decorView.rootWindowSystemWindowInsets(this),
                    width = decorView.width.toDp(),
                )
            }
        }
        decorView.addOnLayoutChangeListener(layoutChangeListener)
        onDispose { decorView.removeOnLayoutChangeListener(layoutChangeListener) }
    }

    return properties
}

fun View.rootWindowSystemWindowInsets(density: Density): PaddingValues {
    return ViewCompat.getRootWindowInsets(this)?.getInsets(WindowInsetsCompat.Type.systemBars())?.let { insets ->
        density.run {
            PaddingValues(
                start = insets.left.toDp(),
                top = insets.top.toDp(),
                end = insets.right.toDp(),
                bottom = insets.bottom.toDp(),
            )
        }
    } ?: PaddingValues(Dp(0F))
}