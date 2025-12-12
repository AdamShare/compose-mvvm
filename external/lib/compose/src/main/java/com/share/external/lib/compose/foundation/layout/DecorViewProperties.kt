package com.share.external.lib.compose.foundation.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger

/** Exposes measured layout and inset values from the Android decor view. */
interface DecorViewProperties {
    /** Measured height of the decor view in [Dp]. */
    val height: Dp

    /** Combined system bar insets (status bar, navigation bar) as
     * [PaddingValues]. */
    val systemInsets: PaddingValues

    /** Measured width of the decor view in [Dp]. */
    val width: Dp
}

/**
 * CompositionLocal providing the current [DecorViewProperties].
 *
 * Must be explicitly set at the root of your composition, typically via the activity module's
 * decorViewProperties extension.
 */
val LocalDecorViewProperties = staticCompositionLocalOf<DecorViewProperties> { DecorViewPropertiesNotSet }

/**
 * Fallback implementation of [DecorViewProperties] used when the provider is not set.
 *
 * Logs a warning and returns exaggerated default values to reduce risk of rendering content offscreen.
 */
private object DecorViewPropertiesNotSet : DecorViewProperties {
    private val logger = Logger.withTag("DecorViewProperties")
    override val height: Dp
        get() {
            logger.a { "DecorViewProperties are not set. Modals may have unexpected layouts." }
            return Int.MAX_VALUE.dp
        }

    override val systemInsets: PaddingValues
        get() {
            logger.a { "DecorViewProperties are not set. Modals may have unexpected layouts." }
            return PaddingValues(0.dp)
        }

    override val width: Dp
        get() {
            logger.a { "DecorViewProperties are not set. Modals may have unexpected layouts." }
            return Int.MAX_VALUE.dp
        }
}
