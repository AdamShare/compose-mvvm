package com.share.sample.integrations.main

import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.navigation.switcher.SingleScopeViewSwitcher
import com.getbackcompose.navigation.switcher.ViewSwitcher
import com.getbackcompose.core.ViewKey

class MainViewSwitcher(scope: ManagedCoroutineScope) :
    ViewSwitcher<ActivityViewRoute> by SingleScopeViewSwitcher(scope = scope, defaultKey = ActivityViewRoute.LoggedOut)

enum class ActivityViewRoute : ViewKey {
    LoggedOut,
    LoggedIn
}
