package com.share.external.lib.mvvm.navigation.dialog

import com.share.external.lib.mvvm.navigation.ComposableProvider

interface DialogComposableProvider : ComposableProvider {
    val properties: DialogProperties get() = DialogProperties()
}