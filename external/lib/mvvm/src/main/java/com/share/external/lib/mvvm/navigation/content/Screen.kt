package com.share.external.lib.mvvm.navigation.content

import androidx.compose.runtime.Stable
import com.share.external.lib.core.ViewProvider

/**
 * A compound interface that represents a complete navigation destination, combining both
 * the UI ([com.share.external.lib.core.View]) and its desired presentation style ([ViewPresentation]).
 */
@Stable
interface Screen : ViewProvider, ViewPresentation
