package com.share.external.foundation.coroutines.test

import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.withTimeoutOrNull

class TestFlow<T>(private val actual: MutableStateFlow<List<T>>) : MutableStateFlow<List<T>> by actual {
    private val current = AtomicInteger(0)

    suspend fun hasNext(timeout: Duration = 100.milliseconds): Boolean {
        // We given the producers a chance to produce the next item.
        return withTimeoutOrNull(timeout) {
            filter { it.size > current.get() }.first()
            true
        } ?: false
    }

    suspend fun has(index: Int, timeout: Duration = 100.milliseconds): Boolean {
        // We given the producers a chance to produce the next item.
        return withTimeoutOrNull(timeout) {
            filter { it.size > index }.first()
            true
        } ?: false
    }

    suspend fun nextValue(): T = get(current.incrementAndGet() - 1)

    suspend fun next(): Event<T> {
        val index = current.incrementAndGet() - 1
        return Event(index = index, value = get(index))
    }

    suspend fun get(index: Int): T = first { it.size > index }[index]

    data class Event<T>(val index: Int, val value: T)
}

fun <T> Flow<T>.test(testScope: TestScope): TestFlow<T> {
    val flow = MutableStateFlow<List<T>>(listOf())
    testScope.backgroundScope.launch { collect { flow.value += it } }
    return TestFlow(flow)
}
