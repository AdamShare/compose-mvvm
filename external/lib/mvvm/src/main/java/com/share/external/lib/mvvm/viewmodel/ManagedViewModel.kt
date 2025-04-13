package com.share.external.lib.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import kotlinx.coroutines.Dispatchers

open class ManagedViewModel(
    name: String,
    scope: ManagedCoroutineScope,
): ViewModel(
    scope.create(name, Dispatchers.Main.immediate),
    AutoCloseable { scope.cancel(awaitChildrenComplete = true) }
)