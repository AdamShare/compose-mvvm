package com.share.external.lib.activity

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.activity.application.ApplicationProvider
import kotlinx.coroutines.CoroutineScope

interface ActivityViewModelContentProvider<in A, out V> : ApplicationProvider {
    fun buildProvider(
        application: A,
        coroutineScope: CoroutineScope
    ): V
}
