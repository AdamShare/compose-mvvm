package com.share.external.lib.navigation.stack

import com.share.external.foundation.coroutines.ManagedCancellable
import com.share.external.lib.view.ViewKey
import com.share.external.lib.view.ViewPresentation
import com.share.external.lib.view.ViewScopeProvider

interface NavigationStackEntryViewProvider: ViewPresentation, ManagedCancellable {
    val navigationKey: ViewKey
    val scopedViewProvider: ViewScopeProvider
}

internal class NavigationStackEntryViewProviderImpl<V: ViewPresentation>(
    override val navigationKey: ViewKey,
    override val scopedViewProvider: ViewScopeProvider,
    viewProvider: V,
): NavigationStackEntryViewProvider,
    ViewPresentation by viewProvider,
    ManagedCancellable by scopedViewProvider
