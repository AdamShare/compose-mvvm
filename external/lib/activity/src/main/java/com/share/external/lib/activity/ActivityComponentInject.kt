package com.share.external.lib.activity

import androidx.activity.ComponentActivity
import com.share.external.lib.activity.application.Injectable

interface ActivityComponentInject<
    ActivityType : ComponentActivity,
    ActivityComponent : Injectable<ActivityType>,
    ActivityComponentFactoryType : ActivityComponentFactory<ActivityType, ActivityComponent>,
    ViewModelComponent : ActivityComponentProvider<ActivityComponentFactoryType>,
> {
    val viewModelComponent: ViewModelComponent

    fun ActivityType.inject() {
        viewModelComponent.activityComponentFactory.inject(this)
    }
}
