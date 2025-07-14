package com.share.external.lib.mvvm.navigation.content

import com.share.external.lib.mvvm.navigation.content.ViewProvider

/**
 * A compound interface that represents a complete navigation destination, combining both the UI ([View]) and its
 * desired presentation style ([ViewPresentation]).
 *
 * This is the most common interface used for screen definitions in a navigation stack.
 */
interface Screen : ViewProvider, ViewPresentation
