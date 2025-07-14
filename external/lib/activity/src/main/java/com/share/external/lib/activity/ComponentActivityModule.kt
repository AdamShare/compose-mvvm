package com.share.external.lib.activity

import android.app.Activity
import androidx.activity.ComponentActivity
import dagger.Module
import dagger.Provides

@Module
object ComponentActivityModule {
    @Provides fun activity(activity: ComponentActivity): Activity = activity
}
