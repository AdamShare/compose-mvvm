package com.share.sample.app.activity

import com.share.external.lib.activity.ActivityComponentFactory
import com.share.external.lib.activity.ActivityScope
import com.share.external.lib.activity.ComponentActivityModule
import com.share.external.lib.activity.application.Injectable
import dagger.Module
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [SampleActivityModule::class])
interface SampleActivityComponent : Injectable<SampleActivity> {

    @Subcomponent.Factory interface Factory : ActivityComponentFactory<SampleActivity, SampleActivityComponent>
}

@Module(includes = [ComponentActivityModule::class]) object SampleActivityModule
