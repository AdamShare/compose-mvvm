package com.share.external.foundation.coroutines

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Creates a new [CoroutineScope] bound to a [SupervisorJob] on the [Dispatchers.Main.immediate] dispatcher.
 *
 * This scope is ideal for UI-related coroutines that must run on the main thread immediately after resuming. Unlike
 * [Dispatchers.Main], which may dispatch coroutines to run on the next main-loop cycle, [Dispatchers.Main.immediate]
 * can resume a coroutine immediately if it is already on the main thread.
 *
 * Using a [SupervisorJob] ensures that one failing child coroutine does not cancel or fail its sibling coroutines. This
 * is especially useful in UI components where you want independent error handling for concurrent tasks.
 *
 * ### Example
 *
 * ```kotlin
 * val mainScope = MainImmediateScope()
 * mainScope.launch {
 *     // Runs immediately on the main thread if already there
 *     updateUi()
 * }
 * ```
 *
 * @return A [CoroutineScope] that runs on [Dispatchers.Main.immediate] with a [SupervisorJob].
 */
fun MainImmediateScope(): CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + CoroutineName("MainImmediate"))
