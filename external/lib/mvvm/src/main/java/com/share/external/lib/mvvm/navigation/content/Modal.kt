package com.share.external.lib.mvvm.navigation.content

import androidx.compose.runtime.Composable
import com.share.external.lib.mvvm.navigation.lifecycle.ViewProvider
import com.share.external.lib.mvvm.navigation.modal.ModalProperties
import kotlinx.coroutines.CoroutineScope

/**
 * A [Screen] that is presented as a modal overlay with configurable [ModalProperties].
 *
 * @param modalProperties Defines the layout, dismissal behavior, and sizing rules for the modal.
 * @param content The composable UI content of the modal.
 */
class Modal(
    modalProperties: ModalProperties = ModalProperties(),
    viewProvider: ViewProvider
) :
    Screen, ViewProvider by viewProvider {
    private val presentation = ViewPresentation.Style.Modal(modalProperties)

    constructor(
        modalProperties: ModalProperties = ModalProperties(),
        view: (CoroutineScope) -> View,
    ): this(
        modalProperties = modalProperties,
        viewProvider = ViewProvider { scope -> view(scope) }
    )

    constructor(
        modalProperties: ModalProperties = ModalProperties(),
        content: @Composable () -> Unit,
    ): this(
        modalProperties = modalProperties,
        viewProvider = ViewProvider { View(content) }
    )

    @Composable override fun preferredPresentationStyle(): ViewPresentation.Style = presentation
}
