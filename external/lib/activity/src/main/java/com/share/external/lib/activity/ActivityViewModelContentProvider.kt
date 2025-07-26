package com.share.external.lib.activity

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.activity.application.ApplicationProvider

interface ActivityViewModelContentProvider<in A, out V> : ApplicationProvider {
    fun buildProvider(
        application: A,
        scope: ManagedCoroutineScope
    ): V
}
