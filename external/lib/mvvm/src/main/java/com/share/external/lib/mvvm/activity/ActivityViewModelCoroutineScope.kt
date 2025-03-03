package com.share.external.lib.mvvm.activity

import com.share.external.lib.mvvm.ComponentCoroutineScope
import kotlinx.coroutines.CoroutineScope

open class ActivityViewModelCoroutineScope(
    parent: CoroutineScope,
    name: String = "ActivityViewModel",
): ComponentCoroutineScope(
    name = name,
    parentScope = parent
)