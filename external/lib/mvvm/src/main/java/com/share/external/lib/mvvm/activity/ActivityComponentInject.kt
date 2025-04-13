package com.share.external.lib.mvvm.activity

import androidx.activity.ComponentActivity
import com.share.external.lib.mvvm.Injectable

interface ActivityComponentInject<
        ActivityType : ComponentActivity,
        ActivityComponent : Injectable<ActivityType>,
        ActivityComponentFactoryType : ActivityComponentFactory<ActivityType, ActivityComponent>,
        ViewModelComponent : ActivityComponentProvider<ActivityComponentFactoryType>
        > {
    val viewModelComponent: ViewModelComponent

    fun ActivityType.inject() {
        viewModelComponent.activityComponentFactory.inject(this)
    }
}