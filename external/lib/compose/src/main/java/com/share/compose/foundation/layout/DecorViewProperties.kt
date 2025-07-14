package com.share.compose.foundation.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import timber.log.Timber

/** Exposes measured layout and inset values from the Android decor view. */
interface DecorViewProperties {
    /** Measured height of the decor view in [androidx.compose.ui.unit.Dp]. */
    val height: Dp

    /** Combined system bar insets (status bar, navigation bar) as
     * [androidx.compose.foundation.layout.PaddingValues]. */
    val systemInsets: PaddingValues

    /** Measured width of the decor view in [androidx.compose.ui.unit.Dp]. */
    val width: Dp
}

/**
 * CompositionLocal providing the current [DecorViewProperties].
 *
 * Must be explicitly set using [com.share.external.lib.mvvm.navigation.modal.decorViewProperties]
 * at the root of your composition.
 */
val LocalDecorViewProperties = staticCompositionLocalOf<DecorViewProperties> { DecorViewPropertiesNotSet }

/**
 * Fallback implementation of [DecorViewProperties] used when the provider is not set.
 *
 * Logs a warning and returns exaggerated default values to reduce risk of rendering content offscreen.
 */
private object DecorViewPropertiesNotSet : DecorViewProperties {
    private const val TAG = "DecorViewProperties"
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
