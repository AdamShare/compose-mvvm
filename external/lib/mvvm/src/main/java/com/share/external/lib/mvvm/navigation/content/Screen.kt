package com.share.external.lib.mvvm.navigation.content

import com.share.external.lib.mvvm.base.ViewProvider

/**
 * A compound interface that represents a complete navigation destination, combining both
 * the UI ([com.share.external.lib.mvvm.base.View]) and its desired presentation style ([ViewPresentation]).
 */
interface Screen : ViewProvider, ViewPresentation
