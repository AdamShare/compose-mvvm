package com.share.external.lib.navigation.stack

import androidx.compose.runtime.Stable
import com.share.external.lib.view.ViewPresentation
import com.share.external.lib.view.ViewProvider

/**
 * A compound interface that represents a complete navigation destination, combining both
 * the UI ([com.share.external.lib.view.View]) and its desired presentation style ([ViewPresentation]).
 */
@Stable
fun interface Screen : ViewProvider, ViewPresentation
