package com.share.dynamic.feature.signin

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.ComposableProvider
import com.share.external.lib.mvvm.navigation.stack.NavigationStackController

class SignInNavigationController(
    scope: ManagedCoroutineScope,
): NavigationStackController<ComposableProvider>(
    analyticsId = "SignInNavigationController",
    scope = scope,
)