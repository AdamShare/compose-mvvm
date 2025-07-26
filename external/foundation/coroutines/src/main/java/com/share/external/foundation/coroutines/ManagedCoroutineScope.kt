package com.share.external.foundation.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A [CoroutineScope] manager that supports hierarchical child scopes and optionally defers its own cancellation until
 * all active child scopes have completed.
 *
 * This interface acts as both a contract for creating custom child scopes (like a [CoroutineScopeFactory]) and a
 * container for those child scopes. It integrates cancellation logic so that the parent scope can decide whether to
 * cancel immediately or wait until all children finish.
 *
 * ### Basic Concept
 * - **Parent Scope:** A `ManagedCoroutineScope` that holds zero or more child scopes.
 * - **Child Scope:** Another `ManagedCoroutineScope` created via [childManagedScope].
 *     - The child remains active until it completes or is cancelled.
 *     - When the child finishes, it is automatically unregistered from the parent.
 * - **Cancellation:** The parent can:
 *     - Cancel immediately, discarding all children.
 *     - Or set a flag to wait for existing children to finish before cancelling.
 *
 * ### Thread Safety
 * - Implementations must ensure modifications to the set of active children are synchronized, so that registration and
 *   unregistration remain thread-safe.
 *
 * ### Usage
 * While this class can be applied to many scenarios that require hierarchical coroutine scopes, one illustrative
 * example is a **navigation stack**:
 * 1. **Create a `ManagedCoroutineScope`** as the parent for a screen or component.
 * 2. **Push a new screen** and call [childManagedScope] to get a child scope for it.
 *         - If the parent is still active, the child starts; otherwise, the child is immediately cancelled.
 * 3. **Pop the screen** (or otherwise decide to remove it):
 *         - Call [cancel] on the parent with `awaitChildrenComplete = true` if you need to wait for all child screens
 *           to complete before final cancellation.
 *         - Or call [cancel] without waiting if you want to terminate immediately.
 *
 * ### `isActive` Usage
 * - If [isActive] is `false`, calls to [cancel] do nothing, and any new [childManagedScope] creation cancels the child
 *   immediately.
 * - If [isActive] is `true`, the parent is still functional, and children can be registered or cancelled normally.
 *
 * ### Cancellation Logic
 * - **Immediate Cancel**: Calling [cancel] with `awaitChildrenComplete = false` clears all children, cancels them, and
 *   then cancels the scope’s job—unless the parent is already inactive.
 * - **Deferred Cancel**: If `awaitChildrenComplete = true` and the scope has active children, the parent defers its
 *   cancellation until all those children unregister themselves (by finishing or being cancelled).
 * - **Multiple Calls**:
 *     1. If the parent is already waiting, further calls to [cancel] with `awaitChildrenComplete = false` will override
 *        that waiting, **unless** the scope is already inactive.
 *     2. If the scope is already inactive, any new call to [cancel] is a no-op.
 *
 * ### Cancellation vs. Completion
 * - **Cancelled**: The parent’s job has been explicitly requested to stop (via [cancel]). Eventually, the job becomes
 *   completed.
 * - **Completed**: The job has fully finished for any reason (including normal completion or final cancellation). Once
 *   the scope is completed, [isActive] is always `false`.
 */
interface ManagedCoroutineScope : CoroutineScopeFactory {

    /**
     * `true` if this scope can still launch coroutines and manage child scopes. Once false, the scope is no longer
     * functional.
     */
    val isActive: Boolean

    /**
     * Registers a completion handler on this scope’s job, which is triggered when the job finishes or is cancelled.
     * This is the same mechanism available in regular [Job] instances via `Job.invokeOnCompletion`.
     *
     * @param handler The callback to invoke upon job completion or cancellation.
     * @return A [DisposableHandle] that can be used to unregister the handler if needed.
     */
    fun invokeOnCompletion(
        context: CoroutineContext = EmptyCoroutineContext,
        handler: (cause: Throwable?) -> Unit,
        ): DisposableHandle

    /**
     * Creates a new [ManagedCoroutineScope] as a child of this scope.
     *
     * If the parent’s job is already completed or cancelled, the new child is immediately cancelled. Otherwise, it is
     * registered as an active child, and the parent waits for it to complete before unregistration.
     *
     * @param name A descriptive name for the child scope (useful for debugging).
     * @param context Additional [CoroutineContext] elements, defaulting to [EmptyCoroutineContext].
     * @return A new `ManagedCoroutineScope` that may be immediately cancelled if the parent is done.
     */
    fun childManagedScope(name: String, context: CoroutineContext = EmptyCoroutineContext): ManagedCoroutineScope

    /**
     * Cancels this scope, either immediately or after waiting for all active children to complete.
     * - If [awaitChildrenComplete] is `false`, or if there are no active children, this function clears all children
     *   and cancels the parent scope immediately.
     * - If [awaitChildrenComplete] is `true` and the scope has active children, the parent defers its cancellation
     *   until all children complete. If a non-blank [message] is provided on the first call, that message is recorded
     *   for final cancellation.
     * - Additional or repeated calls while the parent is waiting can override waiting by passing `awaitChildrenComplete
     *   = false`, unless the scope is already inactive.
     *
     * @param awaitChildrenComplete If `true`, postpone cancellation until current children finish.
     * @param message An optional reason for cancellation; the first non-blank message is retained for final
     *   cancellation.
     */
    fun cancel(awaitChildrenComplete: Boolean = false, message: String = "")
}

/**
 * Creates a new [ManagedCoroutineScope], backed by an internal private implementation.
 *
 * @param actual The underlying [CoroutineScope] used as the parent job context.
 * @return A new [ManagedCoroutineScope] that may create child scopes and optionally defer its own cancellation.
 */
fun ManagedCoroutineScope(actual: CoroutineScope): ManagedCoroutineScope = ManagedCoroutineScopeImpl(actual)

/** Private implementation of [ManagedCoroutineScope]. Exposes no public API outside the interface. */
private class ManagedCoroutineScopeImpl(private val actual: CoroutineScope) : ManagedCoroutineScope {

    // region Configuration / State

    /** Internal set of active child scopes. All modifications are synchronized to prevent concurrency issues. */
    private val activeChildren = mutableSetOf<ManagedCoroutineScope>()

    /** Indicates whether this scope is deferring its own cancellation until active children complete. */
    private var isAwaitingChildrenComplete = false

    /** Optional message recorded when awaiting child completion. The first non-blank message is retained. */
    private var cancellationMessage: String = ""

    /** The underlying job of [actual]. */
    val job: Job = actual.coroutineContext.job

    // endregion

    // region ManagedCoroutineScope Implementation

    override val isActive: Boolean
        get() = actual.isActive

    override fun invokeOnCompletion(context: CoroutineContext, handler: (cause: Throwable?) -> Unit): DisposableHandle {
        return job.invokeOnCompletion(context = context, handler = handler)
    }

    override fun childManagedScope(name: String, context: CoroutineContext): ManagedCoroutineScope {
        // Create a child scope using the factory method from the interface
        val child = ManagedCoroutineScopeImpl(create(name = name, context = context))

        synchronized(activeChildren) {
            // If this scope is inactive, immediately cancel the child.
            if (!isActive || job.isCompleted) {
                val reason =
                    if (job.isCancelled) {
                        "Parent scope is cancelled on childManagedScope"
                    } else {
                        "Parent scope is completed on childManagedScope"
                    }
                child.cancel(message = reason)
                return child
            }
            // Otherwise, track the child
            activeChildren.add(child)
        }

        // Wait for the child to finish, then unregister
        actual.launch { child.job.join() }.invokeOnCompletion { unregisterChild(child) }
        return child
    }

    override fun cancel(awaitChildrenComplete: Boolean, message: String) {
        if (!isActive) {
            // Already inactive, do nothing
            return
        }

        val cancel =
            synchronized(activeChildren) {
                // If we want to wait for children, and we actually have children:
                if (awaitChildrenComplete && activeChildren.isNotEmpty()) {
                    // Only set up waiting once
                    if (!isAwaitingChildrenComplete) {
                        isAwaitingChildrenComplete = true
                        if (message.isNotBlank()) {
                            cancellationMessage = message
                        }
                    }
                    false
                } else {
                    // Cancel everything immediately
                    isAwaitingChildrenComplete = false
                    activeChildren.clear()
                    true
                }
            }
        if (cancel) {
            if (message.isBlank()) {
                actual.cancel()
            } else {
                actual.cancel(message = message)
            }
        }
    }

    // endregion

    // region CoroutineScopeFactory Implementation

    override fun create(name: String, context: CoroutineContext): CoroutineScope {
        // Provide a child scope with a SupervisorJob
        return actual.childSupervisorJobScope(name, context)
    }

    // endregion

    // region Private Helpers

    private fun unregisterChild(child: ManagedCoroutineScopeImpl) {
        val cancel =
            synchronized(activeChildren) {
                activeChildren.remove(child)

                // If we were waiting for children and none remain, cancel now
                if (isActive && isAwaitingChildrenComplete && activeChildren.isEmpty()) {
                    isAwaitingChildrenComplete = false
                    true
                } else {
                    false
                }
            }
        if (cancel) {
            if (cancellationMessage.isBlank()) {
                actual.cancel()
            } else {
                actual.cancel(message = cancellationMessage)
            }
        }
    }

    // endregion
}
