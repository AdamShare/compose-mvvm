package com.share.sample.feature.main

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.navigation.switcher.RetainingScopeViewSwitcher
import com.share.external.lib.navigation.switcher.ViewSwitcher

/**
 * ViewSwitcher for the main tab navigation.
 *
 * Manages switching between Home, Favorites, and Profile tabs.
 * Uses [RetainingScopeViewSwitcher] to preserve tab state when switching.
 */
class MainTabViewSwitcher(scope: ManagedCoroutineScope) :
    ViewSwitcher<TabRoute> by RetainingScopeViewSwitcher(
        scope = scope,
        defaultKey = TabRoute.entries.first()
    )
