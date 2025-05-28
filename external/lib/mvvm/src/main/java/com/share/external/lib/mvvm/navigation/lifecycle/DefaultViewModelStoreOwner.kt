package com.share.external.lib.mvvm.navigation.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.enableSavedStateHandles
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import java.lang.ref.WeakReference
import java.util.UUID

/**
 * A standalone implementation of [ViewModelStoreOwner], [SavedStateRegistryOwner], and [HasDefaultViewModelProviderFactory]
 * designed to provide composition-local scoping for view models, saved state, and lifecycle.
 *
 * This class is used to bridge lifecycle-aware state, ViewModels, and saveable state management into
 * Compose navigation or modal hosting environments that are not tied to an Activity or Fragment.
 *
 * ### Responsibilities
 * - Owns and manages a [ViewModelStore] and [SavedStateRegistry] for a single logical navigation entry.
 * - Initializes and tracks its own [Lifecycle], initially in the `CREATED` state.
 * - Can respond to parent lifecycle changes via [onParentStateChange].
 * - Can be manually cleared via [clear] to release associated ViewModels and transition to `DESTROYED`.
 */
@Immutable
class DefaultViewModelStoreOwner : ViewModelStoreOwner, HasDefaultViewModelProviderFactory, SavedStateRegistryOwner {
    private val defaultFactory by lazy {
        SavedStateViewModelFactory(application = null, owner = this, defaultArgs = null)
    }
    internal val id = UUID.randomUUID().toString()
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
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    /**
     * Transitions this owner to the `DESTROYED` lifecycle state and clears the ViewModel store.
     */
    fun clear() {
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) return
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        viewModelStore.clear()
    }

    /**
     * Updates this owner's lifecycle state to match the parent lifecycle state,
     * but only if it has reached at least the `CREATED` state.
     */
    fun onParentStateChange(state: Lifecycle.State) {
        if (state.isAtLeast(Lifecycle.State.CREATED)) {
            lifecycleRegistry.currentState = state
        }
    }
}

/**
 * Composable function that provides [ViewModelStoreOwner], [SavedStateRegistryOwner], and [LifecycleOwner]
 * scope to the given [content], scoped to this [DefaultViewModelStoreOwner] instance.
 *
 * This enables correct ViewModel scoping and saveable state restoration for screens, modals, or
 * composables that are not hosted by a traditional Activity or Fragment.
 *
 * This function also syncs the [DefaultViewModelStoreOwner]'s lifecycle with its parent's via [DisposableEffect].
 *
 * @param saveableStateHolder Used to preserve state across recompositions and navigation transitions.
 * @param content Composable UI to be scoped within this owner.
 */
@Composable
fun DefaultViewModelStoreOwner.LocalOwnersProvider(
    saveableStateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
    content: @Composable () -> Unit,
) {
    key(id) {
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
        ) {
            saveableStateHolder.SaveableStateProvider(content)
        }
    }
}

/**
 * Wraps [SaveableStateProvider] with logic to persist and remove saveable state
 * using a ViewModel-bound UUID key, scoped to this navigation entry.
 *
 * This logic ensures that saveable state survives recompositions but is correctly cleaned up
 * when the associated navigation entry is removed from the back stack.
 *
 * Copied from AndroidX Navigation Compose.
 */
@Composable
private fun SaveableStateHolder.SaveableStateProvider(content: @Composable () -> Unit) {
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<BackStackEntryIdViewModel>()
    // Stash a reference to the SaveableStateHolder in the ViewModel so that
    // it is available when the ViewModel is cleared, marking the permanent removal of this
    // NavBackStackEntry from the back stack. Which, because of animations,
    // only happens after this leaves composition. Which means we can't rely on
    // DisposableEffect to clean up this reference (as it'll be cleaned up too early)
    viewModel.saveableStateHolderRef = WeakReference(this)
    SaveableStateProvider(viewModel.id, content)
}

/**
 * A ViewModel used to scope and manage saveable state for a navigation back stack entry.
 *
 * This ViewModel generates a unique identifier (UUID) per navigation entry that is restored
 * via [SavedStateHandle]. It also stores a weak reference to the [SaveableStateHolder], which
 * is used to clean up associated state when this entry is removed from the back stack.
 *
 * Copied from AndroidX Navigation Compose internals.
 */
internal class BackStackEntryIdViewModel(handle: SavedStateHandle) : ViewModel() {
    private val idKey = "SaveableStateHolder_BackStackEntryKey"

    // we create our own id for each back stack entry to support multiple entries of the same
    // destination. this id will be restored by SavedStateHandle
    val id: UUID = handle.get<UUID>(idKey) ?: UUID.randomUUID().also { handle.set(idKey, it) }

    lateinit var saveableStateHolderRef: WeakReference<SaveableStateHolder>

    // onCleared will be called on the entries removed from the back stack. here we notify
    // SaveableStateProvider that we should remove any state is had associated with this
    // destination as it is no longer needed.
    override fun onCleared() {
        super.onCleared()
        saveableStateHolderRef.get()?.removeState(id)
        saveableStateHolderRef.clear()
    }
}
