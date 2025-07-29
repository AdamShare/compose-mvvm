package com.share.external.lib.mvvm.navigation.stack

import com.share.external.foundation.coroutines.ManagedCancellable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.content.ViewPresentation
import com.share.external.lib.mvvm.navigation.scope.ViewScopeProvider

interface NavigationStackEntryViewProvider: ViewPresentation, ManagedCancellable {
    val navigationKey: NavigationKey
    val scopedViewProvider: ViewScopeProvider
}

internal class NavigationStackEntryViewProviderImpl<V: ViewPresentation>(
    override val navigationKey: NavigationKey,
    override val scopedViewProvider: ViewScopeProvider,
    viewProvider: V,
): NavigationStackEntryViewProvider,
    ViewPresentation by viewProvider,
    ManagedCancellable by scopedViewProvider
