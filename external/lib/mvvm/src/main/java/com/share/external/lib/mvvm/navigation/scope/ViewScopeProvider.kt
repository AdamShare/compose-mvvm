package com.share.external.lib.mvvm.navigation.scope

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.saveable.SaveableStateHolder
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.core.View
import com.share.external.lib.core.VisibilityScopedView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.ref.WeakReference
import java.util.UUID

@Immutable
open class ViewScopeProvider(
    val name: String,
    onViewAppear: (CoroutineScope) -> View,
    private val scope: ManagedCoroutineScope,
) : ManagedCoroutineScope by scope {
    val id: UUID = UUID.randomUUID()

    internal val view = VisibilityScopedView(
        onViewAppear = onViewAppear,
        scopeFactory = { scope.create(name = name + "Visibility", context = Dispatchers.Main.immediate) },
    )

    private var saveableStateHolderRef: WeakReference<SaveableStateHolder>? = null

    internal fun setSaveableStateHolder(saveableStateHolder: SaveableStateHolder) {
        saveableStateHolderRef = WeakReference(saveableStateHolder)
    }

    override fun cancel(awaitChildrenComplete: Boolean, message: String) {
        saveableStateHolderRef?.run {
            get()?.removeState(id)
            clear()
            saveableStateHolderRef = null
        }
        scope.cancel(awaitChildrenComplete = awaitChildrenComplete, message = message)
    }

    fun interface Factory {
        operator fun invoke(
            name: String,
            onViewAppear: (CoroutineScope) -> View,
            scope: ManagedCoroutineScope
        ): ViewScopeProvider

        companion object {
            val Default = Factory { name, onViewAppear, scope ->
                ViewScopeProvider(name = name, onViewAppear = onViewAppear, scope = scope)
            }
        }
    }
}