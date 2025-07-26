package com.share.external.lib.activity.application

import com.share.external.foundation.coroutines.CoroutineScopeFactory
import com.share.external.foundation.coroutines.childSupervisorJobScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

interface ApplicationCoroutineScopeFactory: CoroutineScopeFactory {
    override fun create(name: String, context: CoroutineContext): CoroutineScope {
        return CoroutineScope(
            context = SupervisorJob() + Dispatchers.Default + CoroutineName("Application")
        ).childSupervisorJobScope(
            name = name,
            context = context
        )
    }
}