package com.share.external.lib.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import kotlinx.coroutines.Dispatchers

open class ManagedViewModel(
    awaitChildrenComplete: Boolean = true,
    name: String = "ManagedViewModel",
    scope: ManagedCoroutineScope,
): ViewModel(
    scope.create(name, Dispatchers.Main.immediate),
    AutoCloseable { scope.cancel(awaitChildrenComplete = awaitChildrenComplete) }
)