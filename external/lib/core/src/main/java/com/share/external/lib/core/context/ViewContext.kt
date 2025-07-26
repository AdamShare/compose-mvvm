package com.share.external.lib.core.context

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
interface ViewContext {
    val foregroundStateFlow: StateFlow<Boolean>
    val isChangingConfigurations: Boolean get() = false

    companion object {
        val EMPTY = object : ViewContext {
            override val foregroundStateFlow: StateFlow<Boolean> = MutableStateFlow(true).asStateFlow()
        }
    }
}

val LocalViewContext = staticCompositionLocalOf { ViewContext.EMPTY }
