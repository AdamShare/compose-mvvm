package com.share.external.lib.activity.application

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ApplicationCoroutineScope internal constructor():
    CoroutineScope by CoroutineScope(context = SupervisorJob() + Dispatchers.Default + CoroutineName("Application"))

