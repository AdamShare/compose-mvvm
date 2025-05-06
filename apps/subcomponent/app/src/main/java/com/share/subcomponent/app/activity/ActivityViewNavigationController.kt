package com.share.subcomponent.app.activity

import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.v1.ScopedViewSwitcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ActivityViewNavigationController(
    scope: CoroutineScope,
): ScopedViewSwitcher<ActivityViewRoute>(
    scope = scope,
    defaultKey = ActivityViewRoute.LoggedOut,
    ) {
    init {
        scope.launch {

        }
    }
}

sealed interface ActivityViewRoute: NavigationKey {
    data object LoggedOut: ActivityViewRoute {
        override val analyticsId: String get() = "LoggedOut"
    }
    data class LoggedIn(val user: String): ActivityViewRoute {
        override val analyticsId: String get() = "LoggedIn"
    }
}