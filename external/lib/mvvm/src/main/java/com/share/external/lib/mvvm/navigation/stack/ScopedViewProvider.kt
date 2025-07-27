package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import java.util.UUID

interface ScopedViewProvider {
    val id: UUID

    val name: String

    /** The view instance that this provider is managing. */
    val content: @Composable (SaveableStateHolder) -> Unit
}
