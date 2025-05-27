package com.share.external.lib.mvvm.application

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ApplicationCoroutineScopeModule {
    @Singleton @Provides fun applicationScope(): ApplicationCoroutineScope = ApplicationCoroutineScope()
}
