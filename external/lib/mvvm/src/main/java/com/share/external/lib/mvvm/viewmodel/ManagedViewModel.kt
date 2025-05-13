package com.share.external.lib.mvvm.viewmodel

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.lifecycle.ViewLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.launch
import timber.log.Timber

open class ManagedViewModel(
    name: String,
    scope: ManagedCoroutineScope,
): StateViewModel(
    scope = scope.create(
        name = name,
        context = Dispatchers.Main.immediate
    )
)

open class ViewLifecycleViewModel(
    name: String,
    scope: ViewLifecycleScope,
): ManagedViewModel(
    name = name,
    scope = scope
) {
    init {
        this.scope.launch {
            scope.viewVisible.dropWhile { !it }.collectLatest {
                if (it) {
                    Timber.tag(name).d("onViewAppear")
                    onViewAppear()
                } else {
                    Timber.tag(name).d("onViewDisappear")
                }
            }
        }
    }

    open suspend fun onViewAppear() {}
}