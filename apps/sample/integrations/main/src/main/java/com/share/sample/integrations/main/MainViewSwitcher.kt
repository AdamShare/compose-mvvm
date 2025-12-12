package com.share.sample.integrations.main

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.navigation.switcher.SingleScopeViewSwitcher
import com.share.external.lib.navigation.switcher.ViewSwitcher
import com.share.external.lib.view.ViewKey

class MainViewSwitcher(scope: ManagedCoroutineScope) :
    ViewSwitcher<ActivityViewRoute> by SingleScopeViewSwitcher(scope = scope, defaultKey = ActivityViewRoute.LoggedOut)

enum class ActivityViewRoute : ViewKey {
    LoggedOut,
    LoggedIn
}
