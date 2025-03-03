package com.share.external.lib.mvvm.activity

interface ActivityViewModelComponent {
    val scope: ActivityViewModelCoroutineScope
}

interface ActivityComponentProvider<Factory> {
    val activityComponentFactory: Factory
}