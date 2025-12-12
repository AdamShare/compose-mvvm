package com.share.external.foundation.coroutines.test

import com.share.external.foundation.coroutines.test.TestFlowCollector.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.ensureActive
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.withTimeoutOrNull

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.test(
    testScope: TestScope,
): TestFlowCollector<T> {
    val flow = MutableStateFlow<List<Event<T>>>(listOf())
    val testFlowCollector = TestFlowCollector(flow)
    testScope.backgroundScope.launch {
        try {
            collect {
                flow.value += Event.Value(it)
            }
        } catch (e: Throwable) {
            ensureActive()
            flow.value += Event.Error(e)
        } finally {
            flow.value += Event.Completed()
        }
    }
    testScope.runCurrent()

    return testFlowCollector
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.toListTest(
    testScope: TestScope,
): List<T> {
    val list = mutableListOf<T>()
    testScope.backgroundScope.launch {
        toList(list)
    }
    testScope.runCurrent()

    return list
}

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
class TestFlowCollector<T>(
    private val actual: MutableStateFlow<List<Event<T>>>,
) : Flow<List<Event<T>>> by actual {
    private val current = AtomicInteger(0)

    val events: List<Event<T>> get() = actual.value

    val values: List<T> get() = events.filter {
        when (it) {
            is Event.Completed<*>,
            is Event.Error<*> -> false
            is Event.Value<*> -> true
        }
    }.map {
        it.value
    }

    val error: Throwable? get() = events.firstNotNullOfOrNull {
        when (it) {
            is Event.Completed<*> -> null
            is Event.Error<*> -> it.error
            is Event.Value<*> -> null
        }
    }

    val completed: Boolean get() = events.lastOrNull() is Event.Completed<*>

    suspend fun hasNext(timeout: Duration = 100.milliseconds): Boolean {
        // We given the producers a chance to produce the next item.
        return withTimeoutOrNull(timeout) {
            actual.filter { it.size > current.get() }.first()
            true
        } ?: false
    }

    suspend fun has(index: Int, timeout: Duration = 100.milliseconds): Boolean {
        // We given the producers a chance to produce the next item.
        return withTimeoutOrNull(timeout) {
            actual.filter { it.size > index }.first()
            true
        } ?: false
    }

    suspend fun nextValue(): T = get(current.incrementAndGet() - 1)

    suspend fun nextIndexedValue(): IndexedValue<T> {
        val index = current.incrementAndGet() - 1
        return IndexedValue(
            index = index,
            value = get(index)
        )
    }

    suspend fun next(): Event<T> = getEvent(current.incrementAndGet() - 1)

    suspend operator fun get(index: Int): T = getEvent(index).value

    suspend fun getEvent(index: Int): Event<T> = actual.first { it.size > index }[index]

    sealed interface Event<out T> {
        val value: T

        class Completed<out T> : Event<T> {
            override val value: T get() = throw IllegalArgumentException("Completed event has no value")
        }
        data class Error<out T>(
            val error: Throwable
        ) : Event<T> {
            override val value: T get() = throw error
        }
        data class Value<out T>(
            override val value: T,
        ) : Event<T>
    }
}
