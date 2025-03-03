package com.share.external.lib.mvvm.navigation.dialog

import com.share.external.lib.mvvm.navigation.ComposableProvider

class DefaultDialogComposableProvider(
    override val properties: DialogProperties = DialogProperties(),
    composableProvider: ComposableProvider
) : DialogComposableProvider, ComposableProvider by composableProvider