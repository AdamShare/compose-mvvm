package com.share.external.foundation.coroutines

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CoroutineScopeExtTest {
    @Test
    fun testParentScopeCancel() = runTest {
        val scope = CoroutineScope(SupervisorJob() + CoroutineName("Parent"))
        val scope1 = scope.childSupervisorJobScope("Test Scope 1")
        val scope2 = scope1.childSupervisorJobScope("Test Scope 2")

        assertEquals(
            scope1.coroutineContext[CoroutineName.Key]?.name?.removeInParentheses(),
                "Parent⇨Test Scope 1(hash)".removeInParentheses()
        )
        assertEquals(
            scope2.coroutineContext[CoroutineName.Key]?.name?.removeInParentheses(),
            "Parent⇨Test Scope 1(hash)⇨Test Scope 2(hash)".removeInParentheses()
        )

        val scope2LaunchJob = scope2.launch { awaitCancellation() }
        val scope1LaunchJob = scope1.launch { awaitCancellation() }
        val scopeLaunchJob = scope.launch { awaitCancellation() }

        scope.cancel()

        assertTrue(scopeLaunchJob.isCancelled)
        assertTrue(scope1LaunchJob.isCancelled)
        assertTrue(scope2LaunchJob.isCancelled)
    }

    @Test
    fun testChildScopeCancel() = runTest {
        val scope = CoroutineScope(SupervisorJob())
        val scope1 = scope.childSupervisorJobScope("Test Scope 1")
        val scope2 = scope1.childSupervisorJobScope("Test Scope 2")

        val scope2LaunchJob = scope2.launch { awaitCancellation() }
        val scope1LaunchJob = scope1.launch { awaitCancellation() }
        val scopeLaunchJob = scope.launch { awaitCancellation() }

        scope2.cancel()

        assertFalse(scopeLaunchJob.isCancelled)
        assertFalse(scope1LaunchJob.isCancelled)
        assertTrue(scope2LaunchJob.isCancelled)

        scope.cancel()

        assertTrue(scopeLaunchJob.isCancelled)
        assertTrue(scope1LaunchJob.isCancelled)
        assertTrue(scope2LaunchJob.isCancelled)
    }

    @Test
    fun testChildScopeCancelsChild() = runTest {
        val scope = CoroutineScope(SupervisorJob())
        val scope1 = scope.childSupervisorJobScope("Test Scope 1")
        val scope2 = scope1.childSupervisorJobScope("Test Scope 2")

        val scope2LaunchJob = scope2.launch { awaitCancellation() }
        val scope1LaunchJob = scope1.launch { awaitCancellation() }
        val scopeLaunchJob = scope.launch { awaitCancellation() }

        scope1.cancel()

        assertFalse(scopeLaunchJob.isCancelled)
        assertTrue(scope1LaunchJob.isCancelled)
        assertTrue(scope2LaunchJob.isCancelled)

        scope.cancel()

        assertTrue(scopeLaunchJob.isCancelled)
        assertTrue(scope1LaunchJob.isCancelled)
        assertTrue(scope2LaunchJob.isCancelled)
    }
}

private fun String.removeInParentheses() = replace(Regex("\\([^)]*\\)"), "()")