package com.share.external.lib.mvvm.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import javax.inject.Provider

@Composable
inline fun <reified VM : ViewModel> Provider<VM>.viewModel(): VM {
    return viewModel(this)
}

@Composable
inline fun <reified VM : ViewModel> viewModel(
    provider: Provider<VM>,
): VM = androidx.lifecycle.viewmodel.compose.viewModel(
    factory = provider.toViewModelProviderFactory()
)

@Composable
inline fun <reified VM : ViewModel> componentViewModel(
    crossinline componentFactory: () -> Provider<VM>,
): VM = viewModel(
    factory = { componentFactory().get() }
)

fun <VM : ViewModel, T : VM> Provider<T>.toViewModelFactory(): ViewModelFactory<VM> {
    return ViewModelFactory { get() }
}

fun <VM : ViewModel> Provider<VM>.toViewModelProviderFactory(): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T = get() as T
    }
}
