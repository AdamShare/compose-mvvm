package com.share.dynamic.app.activity

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.switcher.ScopedViewSwitcher
import com.share.external.lib.mvvm.navigation.switcher.ViewSwitcher
import kotlinx.coroutines.launch

class ActivityViewNavigationController(
    scope: ManagedCoroutineScope,
): ViewSwitcher<ActivityViewRoute> by ScopedViewSwitcher(
    scope = scope,
    defaultKey = ActivityViewRoute.LoggedOut,
) {
    private val coroutineScope = scope.create(TAG)

    init {
        coroutineScope.launch {

        }
    }

    companion object {
        private const val TAG = "ActivityViewNavigationController"
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