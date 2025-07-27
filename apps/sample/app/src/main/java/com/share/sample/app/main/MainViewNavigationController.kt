package com.share.sample.app.main

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.switcher.ScopedViewSwitcher
import com.share.external.lib.mvvm.navigation.switcher.ViewSwitcher

class MainViewNavigationController(scope: ManagedCoroutineScope) :
    ViewSwitcher<ActivityViewRoute> by ScopedViewSwitcher(scope = scope, defaultKey = ActivityViewRoute.LoggedOut)

sealed interface ActivityViewRoute : NavigationKey {
    data object LoggedOut : ActivityViewRoute {
        override val name: String
            get() = "LoggedOut"
    }

    data class LoggedIn(val user: String) : ActivityViewRoute {
        override val name: String
            get() = "LoggedIn"
    }
}
