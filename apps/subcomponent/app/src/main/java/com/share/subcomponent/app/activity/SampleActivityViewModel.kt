package com.share.subcomponent.app.activity

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.activity.ActivityViewModel

class SampleActivityViewModel(
    activityComponent: SampleActivityComponent.Factory,
    componentCoroutineScope: ManagedCoroutineScope,
): ActivityViewModel<SampleActivity, SampleActivityComponent>(
    activityComponentFactory = activityComponent,
    componentCoroutineScope = componentCoroutineScope
)
