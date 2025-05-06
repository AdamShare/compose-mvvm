package com.share.external.lib.mvvm.navigation.content

import com.share.external.lib.mvvm.navigation.dialog.DialogProperties

/**
 * The [DisplayMode] is used to determine how the [CustomerDisplayComposableProvider] should be displayed in the stack.
 */
sealed interface DisplayMode {

    /**
     * At least one [FullScreen] state will always be attached on the top of the backstack,
     * In case if there is [Overlay] state, the overlay will be attached over the latest [FullScreen].
     */
    data object FullScreen : DisplayMode

    /**
     * The [Overlay] state will be attached over the latest [FullScreen] state and all overlays above.
     */
    data class Overlay(val properties: DialogProperties? = DialogProperties()) : DisplayMode
}