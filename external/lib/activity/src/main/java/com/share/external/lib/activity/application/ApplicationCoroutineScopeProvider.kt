package com.share.external.lib.activity.application

interface ApplicationCoroutineScopeProvider {
    val applicationCoroutineScope: ApplicationCoroutineScope get() = ApplicationCoroutineScope()
}