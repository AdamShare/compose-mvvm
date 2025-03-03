package com.share.external.lib.mvvm.viewmodel

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import javax.inject.Provider

fun interface ViewModelFactory<VM> {
    fun create(savedStateHandle: SavedStateHandle): VM
}

inline fun <VM : ViewModel> viewModelFactory(
    crossinline create: () -> VM
): ViewModelFactory<VM> = ViewModelFactory { create() }

@Composable
inline fun <reified VM : ViewModel> viewModel(
    factory: ViewModelFactory<VM>,
): VM = viewModel(
    factory = factory.toViewModelProviderFactory()
)

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.viewModel(
    factory: ViewModelFactory<VM>
): Lazy<VM> = viewModels(
    factoryProducer = { factory.toViewModelProviderFactory() }
)

fun <VM : ViewModel> ViewModelFactory<VM>.toViewModelProviderFactory(): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T = create(extras.createSavedStateHandle()) as T
    }
}