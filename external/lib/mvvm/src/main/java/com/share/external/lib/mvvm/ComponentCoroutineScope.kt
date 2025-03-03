package com.share.external.lib.mvvm

import com.share.external.foundation.coroutines.childSupervisorJobScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class ComponentCoroutineScope(
    name: String,
    parentScope: CoroutineScope
): CoroutineScope by parentScope.childSupervisorJobScope(
    context = Dispatchers.IO,
    name = name,
)