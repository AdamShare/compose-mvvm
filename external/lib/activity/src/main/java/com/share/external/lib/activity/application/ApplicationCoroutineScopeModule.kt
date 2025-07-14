package com.share.external.lib.activity.application

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ApplicationCoroutineScopeModule {
    @Singleton @Provides fun applicationScope(): ApplicationCoroutineScope = ApplicationCoroutineScope()
}
