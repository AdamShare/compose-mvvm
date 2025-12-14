package com.getbackcompose.navigation.stack

import com.getbackcompose.foundation.coroutines.ManagedCancellable
import com.getbackcompose.core.ViewKey
import com.getbackcompose.core.ViewPresentation
import com.getbackcompose.core.ViewScopeProvider

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
