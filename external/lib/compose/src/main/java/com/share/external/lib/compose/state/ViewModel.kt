package com.share.external.lib.compose.state

import com.share.external.lib.compose.runtime.LoggingStateChangeObserver
import com.share.external.foundation.coroutines.CoroutineScopeFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class ViewModel(override val scope: CoroutineScope) : StateProvider, LoggingStateChangeObserver {
    constructor(
        name: String,
        scope: CoroutineScopeFactory,
    ) : this(scope = scope.create(name = name, context = Dispatchers.Main.immediate))
}