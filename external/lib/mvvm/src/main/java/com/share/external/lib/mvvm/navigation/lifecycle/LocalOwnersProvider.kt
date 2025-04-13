package com.share.external.lib.mvvm.navigation.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import java.lang.ref.WeakReference
import java.util.UUID

/**
 * Provides scoping to compose for [DefaultViewModelStoreOwner]
 */
@Composable
fun LocalDefaultViewModelStoreOwner(
    owner: DefaultViewModelStoreOwner,
    saveableStateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
    content: @Composable () -> Unit
) {
    key(owner.id) {
        /** Forward container lifecycle events to owner */
        RegisterLifecycleObserver(owner)

        CompositionLocalProvider(
            LocalViewModelStoreOwner provides owner,
            LocalLifecycleOwner provides owner,
            LocalSavedStateRegistryOwner provides owner
        ) {
            SaveableStateProvider(
                holder = saveableStateHolder,
                content = content
            )
        }
    }
}

/**
 * Observe lifecycle events from [LocalLifecycleOwner]
 */
@Composable
private fun RegisterLifecycleObserver(observer: LifecycleEventObserver) {
    val currentLifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(observer, currentLifecycleOwner) {
        currentLifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            currentLifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * Logic copied from androidx navigation
 */
@Composable
private fun SaveableStateProvider(
    holder: SaveableStateHolder,
    content: @Composable () -> Unit
) {
    val viewModel = viewModel<BackStackEntryIdViewModel>()
    // Stash a reference to the SaveableStateHolder in the ViewModel so that
    // it is available when the ViewModel is cleared, marking the permanent removal of this
    // NavBackStackEntry from the back stack. Which, because of animations,
    // only happens after this leaves composition. Which means we can't rely on
    // DisposableEffect to clean up this reference (as it'll be cleaned up too early)
    viewModel.saveableStateHolderRef = WeakReference(holder)
    holder.SaveableStateProvider(viewModel.id, content)
}

/**
 * Logic copied from androidx navigation
 */
internal class BackStackEntryIdViewModel(handle: SavedStateHandle) : ViewModel() {
    private val idKey = "SaveableStateHolder_BackStackEntryKey"

    // we create our own id for each back stack entry to support multiple entries of the same
    // destination. this id will be restored by SavedStateHandle
    val id: UUID = handle.get<UUID>(idKey) ?: UUID.randomUUID().also { handle[idKey] = it }

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