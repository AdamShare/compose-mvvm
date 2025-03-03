package com.share.external.lib.mvvm.activity

import androidx.lifecycle.ViewModel
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.Injectable
import com.share.external.lib.mvvm.viewmodel.ManagedViewModel
import kotlinx.coroutines.Dispatchers

open class ActivityViewModel<A, C: Injectable<A>>(
    val activityComponentFactory : ActivityComponentFactory<A, C>,
    componentCoroutineScope: ManagedCoroutineScope,
): ViewModel(
    componentCoroutineScope.create(
        name = "ActivityViewModel",
        context = Dispatchers.Main.immediate
    ),
    AutoCloseable {
        componentCoroutineScope.cancel(awaitChildrenComplete = false)
    }
)
