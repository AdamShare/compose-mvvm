package com.share.external.lib.mvvm.navigation.content

import androidx.compose.runtime.Composable
import com.share.external.lib.mvvm.navigation.modal.ModalProperties

/**
 * A [Screen] that is presented as a modal overlay with configurable [ModalProperties].
 *
 * @param modalProperties Defines the layout, dismissal behavior, and sizing rules for the modal.
 * @param content The composable UI content of the modal.
 */
class Modal(modalProperties: ModalProperties = ModalProperties(), override val content: @Composable () -> Unit) :
    Screen {
    private val presentation = ViewPresentation.Style.Modal(modalProperties)

    @Composable override fun preferredPresentationStyle(): ViewPresentation.Style = presentation
}
