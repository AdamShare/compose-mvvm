package com.getbackcompose.navigation.stack

import androidx.compose.runtime.Stable
import com.getbackcompose.core.ViewPresentation
import com.getbackcompose.core.ViewProvider

/**
 * A compound interface that represents a complete navigation destination, combining both
 * the UI ([com.getbackcompose.core.View]) and its desired presentation style ([ViewPresentation]).
 */
@Stable
fun interface Screen : ViewProvider, ViewPresentation
