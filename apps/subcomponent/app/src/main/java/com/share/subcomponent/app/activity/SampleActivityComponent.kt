package com.share.subcomponent.app.activity

import com.share.external.lib.mvvm.Injectable
import com.share.external.lib.mvvm.activity.ActivityComponentFactory
import com.share.external.lib.mvvm.activity.ActivityScope
import com.share.external.lib.mvvm.activity.ComponentActivityModule
import dagger.Module
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [SampleActivityModule::class])
interface SampleActivityComponent: Injectable<SampleActivity> {

    @Subcomponent.Factory
    interface Factory: ActivityComponentFactory<SampleActivity, SampleActivityComponent>
}

@Module(includes = [ComponentActivityModule::class])
object SampleActivityModule