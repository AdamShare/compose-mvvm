package com.share.external.lib.mvvm.activity

import androidx.activity.ComponentActivity
import com.share.external.lib.mvvm.application.Injectable
import dagger.BindsInstance

interface ActivityComponentFactory<A, C: Injectable<A>> {
    operator fun invoke(@BindsInstance activity: ComponentActivity): C
}

fun <A: ComponentActivity, C: Injectable<A>> ActivityComponentFactory<A, C>.inject(
    instance: A
) = invoke(instance).inject(instance)