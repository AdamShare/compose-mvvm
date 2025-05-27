package com.share.external.foundation.coroutines.test

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/** A JUnit [TestRule] that sets the Main dispatcher to [testDispatcher] for the duration of the test. */
@OptIn(ExperimentalCoroutinesApi::class)
class TestScopeRule
private constructor(
    private val overrideMainDispatcher: Boolean,
    private val scope: TestScope,
    private val testDispatcher: TestDispatcher,
) : TestWatcher(), CoroutineScope by scope {
    val backgroundScope: CoroutineScope
        get() = scope.backgroundScope

    constructor(
        overrideMainDispatcher: Boolean = false,
        testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
    ) : this(
        overrideMainDispatcher = overrideMainDispatcher,
        scope = TestScope(testDispatcher),
        testDispatcher = testDispatcher,
    )

    override fun starting(description: Description) {
        if (overrideMainDispatcher) {
            Dispatchers.setMain(testDispatcher)
        }
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }

    fun runTest(timeout: Duration = 10.seconds, testBody: suspend TestScope.() -> Unit) {
        scope.runTest(timeout, testBody)
    }
}
