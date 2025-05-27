package com.share.external.lib.mvvm.application

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ApplicationCoroutineScope :
    CoroutineScope by CoroutineScope(context = SupervisorJob() + Dispatchers.IO + CoroutineName("Application"))
