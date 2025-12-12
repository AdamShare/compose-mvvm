package com.share.external.lib.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.enableSavedStateHandles
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedState
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

/**
 * A standalone implementation of [ViewModelStoreOwner], [SavedStateRegistryOwner], and
 * [HasDefaultViewModelProviderFactory] designed to provide composition-local scoping for view models, saved state, and
 * lifecycle.
 *
 * This class is used to bridge lifecycle-aware state, ViewModels, and saveable state management into Compose navigation
 * or modal hosting environments that are not tied to an Activity or Fragment.
 *
 * ### Responsibilities
 * - Owns and manages a [ViewModelStore] and [SavedStateRegistry] for a single logical navigation entry.
 * - Initializes and tracks its own [Lifecycle], initially in the `CREATED` state.
 * - Can respond to parent lifecycle changes via [onParentStateChange].
 * - Can be manually cleared via [clear] to release associated ViewModels and transition to `DESTROYED`.
 */
@Immutable
class DefaultViewModelStoreOwner(savedState: SavedState?) :
    ViewModelStoreOwner, HasDefaultViewModelProviderFactory, SavedStateRegistryOwner {
    private val defaultFactory by lazy {
        SavedStateViewModelFactory(application = null, owner = this, defaultArgs = null)
    }
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val viewModelStore: ViewModelStore = ViewModelStore()
    override val lifecycle: Lifecycle = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory = defaultFactory
    override val defaultViewModelCreationExtras: CreationExtras
        get() {
            val extras = MutableCreationExtras()
            extras[SAVED_STATE_REGISTRY_OWNER_KEY] = this
            extras[VIEW_MODEL_STORE_OWNER_KEY] = this
            return extras
        }

    init {
        enableSavedStateHandles()
        savedStateRegistryController.performRestore(savedState)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED

    }

    /** Transitions this owner to the `DESTROYED` lifecycle state and clears the ViewModel store. */
    fun clear() {
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) return
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        viewModelStore.clear()
    }

    /**
     * Updates this owner's lifecycle state to match the parent lifecycle state, but only if it has reached at least the
     * `CREATED` state.
     */
    fun onParentStateChange(state: Lifecycle.State) {
        if (state.isAtLeast(Lifecycle.State.CREATED)) {
            lifecycleRegistry.currentState = state
        }
    }
}

/**
 * Composable function that provides [ViewModelStoreOwner], [SavedStateRegistryOwner], and [LifecycleOwner] scope to the
 * given [content], scoped to this [DefaultViewModelStoreOwner] instance.
 *
 * This enables correct ViewModel scoping and saveable state restoration for screens, modals, or composables that are
 * not hosted by a traditional Activity or Fragment.
 *
 * This function also syncs the [DefaultViewModelStoreOwner]'s lifecycle with its parent's via [DisposableEffect].
 *
 * @param saveableStateHolder Used to preserve state across recompositions and navigation transitions.
 * @param content Composable UI to be scoped within this owner.
 */
@Composable
fun DefaultViewModelStoreOwner.LocalOwnersProvider(content: @Composable () -> Unit) {
    val currentLifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(this, currentLifecycleOwner) {
        val observer = LifecycleEventObserver { _, event -> onParentStateChange(event.targetState) }
        currentLifecycleOwner.lifecycle.addObserver(observer)
        onDispose { currentLifecycleOwner.lifecycle.removeObserver(observer) }
    }

    CompositionLocalProvider(
        LocalViewModelStoreOwner provides this,
        LocalLifecycleOwner provides this,
        LocalSavedStateRegistryOwner provides this,
        content = content,
    )
}
