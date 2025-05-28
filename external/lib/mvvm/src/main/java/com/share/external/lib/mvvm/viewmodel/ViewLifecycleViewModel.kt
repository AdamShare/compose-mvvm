package com.share.external.lib.mvvm.viewmodel

import com.share.external.foundation.coroutines.CoroutineScopeFactory
import com.share.external.lib.mvvm.navigation.lifecycle.ViewLifecycle
import com.share.external.lib.mvvm.navigation.stack.NavigationStackScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class ViewLifecycleViewModel(override val scope: CoroutineScope, viewLifecycle: ViewLifecycle) :
    ViewModel(scope), ViewLifecycle by viewLifecycle, ViewStateProvider {
    constructor(
        name: String,
        scope: CoroutineScopeFactory,
        viewLifecycle: ViewLifecycle,
    ) : this(scope = scope.create(name = name, context = Dispatchers.Main.immediate), viewLifecycle = viewLifecycle)

    constructor(
        name: String,
        scope: NavigationStackScope<*>,
    ) : this(scope = scope.create(name = name, context = Dispatchers.Main.immediate), viewLifecycle = scope)
}
