package com.share.external.lib.navigation.stack

import androidx.compose.runtime.Stable
import com.share.external.lib.view.ViewKey
import kotlinx.coroutines.Job

/**
 * Contract for a minimal navigation back stack that supports only **backward navigation**.
 *
 * This is used to expose a limited interface to nested or delegated navigation stacks, ensuring encapsulation by hiding
 * forward navigation capabilities (e.g., push).
 *
 * Must be invoked on the **main thread**.
 */
@Stable
interface NavigationBackStack {

    /**
     * The current number of entries in the back stack.
     *
     * Returns `0` when the stack is empty.
     */
    val size: Int

    /**
     * Pops the top-most entry from the stack.
     *
     * @return `true` if the stack was mutated (i.e., an element was removed), `false` if the stack was already empty.
     */
    fun pop(): Boolean

    /**
     * Pops entries from the stack until the specified [key] is at the top.
     *
     * If [inclusive] is `true`, the [key] itself will also be removed.
     *
     * @param key The navigation key to pop to.
     * @param inclusive Whether to remove the [key] as well.
     * @return `true` if the stack was mutated, `false` if the [key] was not found or no changes were made.
     */
    fun popTo(key: ViewKey, inclusive: Boolean = false): Boolean

    /**
     * Removes the specified [key] from the stack, regardless of its position.
     *
     * @param key The navigation key to remove.
     *
     * No-op if the [key] is not found in the stack.
     */
    fun remove(key: ViewKey)

    /**
     * Removes all entries from the stack, leaving it empty.
     *
     * No-op if the stack is already empty.
     */
    fun removeAll()

    /**
     * Executes a batch of navigation operations as a single transaction.
     *
     * During a transaction, state updates are deferred until all operations complete,
     * reducing unnecessary recompositions and ensuring consistent state.
     *
     * @param block The suspend block containing navigation operations to execute atomically.
     * @return A [Job] that completes when the transaction finishes.
     */
    suspend fun transaction(block: suspend () -> Unit)
}
