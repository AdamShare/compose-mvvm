package com.share.external.lib.mvvm.activity

import com.share.external.lib.mvvm.application.ApplicationProvider

interface ActivityViewModelComponentProvider<ViewModelComponent> : ApplicationProvider {
    fun buildViewModelComponent(): ViewModelComponent
}
