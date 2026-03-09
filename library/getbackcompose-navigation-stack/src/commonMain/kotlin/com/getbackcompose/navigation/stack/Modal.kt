package com.getbackcompose.navigation.stack

import androidx.compose.runtime.Composable
import com.getbackcompose.compose.modal.ModalProperties
import com.getbackcompose.core.View
import com.getbackcompose.core.ViewPresentation
import com.getbackcompose.core.ViewProvider
import kotlinx.coroutines.CoroutineScope

/**
 * A [Screen] that is presented as a modal overlay with configurable [ModalProperties].
 *
 * @param modalProperties Defines the layout, dismissal behavior, and sizing rules for the modal.
 * @param viewProvider View provider containing the content for the modal.
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

    override val preferredPresentationStyle = @Composable { presentation }
}
