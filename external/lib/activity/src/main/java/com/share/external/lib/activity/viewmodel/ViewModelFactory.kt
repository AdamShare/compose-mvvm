package com.share.external.lib.activity.viewmodel

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

/**
 * Factory interface for creating ViewModels with access to [SavedStateHandle].
 *
 * This simplifies ViewModel creation in Activities by providing a functional interface
 * that can be used with trailing lambda syntax.
 *
 * @param VM The type of ViewModel to create.
 */
fun interface ViewModelFactory<VM> {
    /**
     * Creates the ViewModel instance.
     *
     * @param savedStateHandle Handle for accessing saved state across process death.
     * @return The created ViewModel instance.
     */
    fun create(savedStateHandle: SavedStateHandle): VM
}

/**
 * Creates a lazy delegate for a ViewModel using the provided factory.
 *
 * This is a convenience extension that wraps AndroidX's [viewModels] with simpler
 * factory syntax using trailing lambdas.
 *
 * ### Usage
 * ```kotlin
 * class MyActivity : ComponentActivity() {
 *     private val myViewModel by viewModel { savedState ->
 *         MyViewModel(savedState, repository)
 *     }
 * }
 * ```
 *
 * @param factory Factory that creates the ViewModel with access to [SavedStateHandle].
 * @return A lazy delegate that creates the ViewModel when first accessed.
 */
@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.viewModel(factory: ViewModelFactory<VM>): Lazy<VM> =
    viewModels(factoryProducer = { factory.toViewModelProviderFactory() })

/**
 * Converts this [ViewModelFactory] to a standard [ViewModelProvider.Factory].
 */
fun <VM : ViewModel> ViewModelFactory<VM>.toViewModelProviderFactory(): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
            create(extras.createSavedStateHandle()) as T
    }
}
