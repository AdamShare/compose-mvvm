package com.share.external.lib.activity

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.foundation.coroutines.childSupervisorJobScope
import com.share.external.lib.activity.application.ApplicationCoroutineScope
import kotlinx.coroutines.Dispatchers

open class ActivityViewModelCoroutineScope(parent: ApplicationCoroutineScope, name: String = "ActivityViewModel") :
    ManagedCoroutineScope by ManagedCoroutineScope(
        parent.childSupervisorJobScope(name = name, context = Dispatchers.IO)
    )
