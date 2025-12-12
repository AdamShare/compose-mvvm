package com.share.external.lib.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.share.external.lib.compose.modal.ModalProperties

/**
 * Describes how a [View] prefers to be presented: full-screen or as a modal.
 *
 * Used by navigation hosts to determine how to render views in a stack, allowing different types of transitions,
 * overlays, or layout constraints.
 */
@Stable
interface ViewPresentation {
    /**
     * Returns the preferred [Style] in which this view should be presented.
     *
     * The default implementation returns [Style.FullScreen], but this can be overridden to return a [Style.Modal] with
     * custom [ModalProperties].
     */
    val preferredPresentationStyle: @Composable () -> Style get() = Style.Default

    /** Defines the visual presentation mode for a view. */
    sealed interface Style {
        /** Indicates the view should occupy the full screen. */
        data object FullScreen : Style

        /**
         * Indicates the view should be shown as a modal, optionally configured with layout and behavior properties via
         * [ModalProperties].
         */
        data class Modal(val properties: ModalProperties? = ModalProperties()) : Style

        companion object {
            val Default = @Composable { FullScreen }
        }
    }
}
