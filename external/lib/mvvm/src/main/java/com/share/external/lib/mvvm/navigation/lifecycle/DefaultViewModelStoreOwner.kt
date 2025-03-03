package com.share.external.lib.mvvm.navigation.lifecycle

import androidx.compose.runtime.Immutable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.enableSavedStateHandles
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import java.util.UUID

@Immutable
class DefaultViewModelStoreOwner :
    ViewModelStoreOwner,
    HasDefaultViewModelProviderFactory,
    SavedStateRegistryOwner,
    LifecycleEventObserver {
    private val defaultFactory by lazy {
        SavedStateViewModelFactory(
            application = null,
            owner = this,
            defaultArgs = null,
        )
    }
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    internal val id = UUID.randomUUID().toString()

    override val viewModelStore: ViewModelStore = ViewModelStore()
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry
    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = defaultFactory
    override val defaultViewModelCreationExtras: CreationExtras
        get() {
            val extras = MutableCreationExtras()
            extras[SAVED_STATE_REGISTRY_OWNER_KEY] = this
            extras[VIEW_MODEL_STORE_OWNER_KEY] = this
            return extras
        }

    init {
        enableSavedStateHandles()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun clear() {
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        viewModelStore.clear()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event.targetState.isAtLeast(Lifecycle.State.CREATED)) {
            lifecycleRegistry.currentState = event.targetState
        }
    }
}
