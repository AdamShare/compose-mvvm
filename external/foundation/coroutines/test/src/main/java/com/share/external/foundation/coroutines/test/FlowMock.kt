package com.share.external.foundation.coroutines.test

import io.mockk.CapturingSlot
import io.mockk.MockKMatcherScope
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first

inline fun <reified T> Flow<T>.emit(vararg values: T) {
    emitAll(values.asFlow())
}

inline fun <reified T> Flow<T>.emitAll(flow: Flow<T>) {
    coEvery { collect(any()) } coAnswers {
        flow.collect(firstArg())
    }
}

inline fun <reified T> StateFlow<T>.emitAll(stateFlow: Flow<T>) {
    every { value } coAnswers {
        stateFlow.first()
    }

    emitAll(flow = stateFlow)
}

inline fun <reified T> StateFlow<T>.emit(vararg values: T) {
    emitAll(stateFlow = values.asFlow())
}

inline fun <reified T : Any> MockKMatcherScope.anyResult(): Result<T> = Result.success(any())
inline fun <reified T : Any> MockKMatcherScope.anyFailure(): Result<T> = Result.failure(any())
inline fun <reified T : Any> MockKMatcherScope.captureSuccess(
    lst: CapturingSlot<T>
): Result<T> = Result.success(capture(lst))

inline fun <reified T : Any, reified E : Throwable> MockKMatcherScope.captureFailure(
    lst: CapturingSlot<E>
): Result<T> = Result.failure(capture(lst))