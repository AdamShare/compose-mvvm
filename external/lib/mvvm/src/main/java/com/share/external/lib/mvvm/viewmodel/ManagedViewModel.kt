package com.share.external.lib.mvvm.viewmodel

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import kotlinx.coroutines.Dispatchers

open class ManagedViewModel(
    name: String,
    scope: ManagedCoroutineScope,
): StateViewModel(
    scope.create(
        name = name,
        context = Dispatchers.Main.immediate
    ),
    AutoCloseable {
        scope.cancel(awaitChildrenComplete = true)
    }
)