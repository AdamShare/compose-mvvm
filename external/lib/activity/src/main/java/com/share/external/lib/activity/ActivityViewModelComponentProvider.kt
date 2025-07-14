package com.share.external.lib.activity

import com.share.external.lib.activity.application.ApplicationProvider

interface ActivityViewModelComponentProvider<ViewModelComponent> : ApplicationProvider {
    fun buildViewModelComponent(): ViewModelComponent
}
