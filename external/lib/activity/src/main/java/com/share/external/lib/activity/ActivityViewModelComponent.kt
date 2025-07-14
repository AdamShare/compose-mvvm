package com.share.external.lib.activity

interface ActivityViewModelComponent {
    val scope: ActivityViewModelCoroutineScope
}

interface ActivityComponentProvider<Factory> {
    val activityComponentFactory: Factory
}
