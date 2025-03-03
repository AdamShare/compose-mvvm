package com.share.external.lib.mvvm.viewmodel

import androidx.lifecycle.ViewModel

interface ViewModelComponent<VM: ViewModel> {
    val viewModel: VM
}