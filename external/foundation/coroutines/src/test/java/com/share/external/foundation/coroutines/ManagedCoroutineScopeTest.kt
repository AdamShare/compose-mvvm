package com.share.external.foundation.coroutines

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ManagedCoroutineScopeTest {
    /**
     * 1) Child is active if the parent is active, but becomes inactive when the parent cancels immediately.
     */
    @Test
    fun `childManagedScope active if parent is active`() = runTest {
        // Use backgroundScope as the underlying scope
        val parent = ManagedCoroutineScope(backgroundScope)

        // Create a child managed scope
        val child = parent.childManagedScope("ChildA")

        // From the child, create a regular CoroutineScope
        val childWorkScope = child.create("ChildWork")

        // Launch a coroutine that awaits cancellation (so it stays active until cancelled)
        val job = childWorkScope.launch {
            awaitCancellation()
        }

        // Initially, they're all active
        assertTrue("Expected parent to be active", parent.isActive)
        assertTrue("Expected child scope to be active", child.isActive)
        assertTrue("Expected childWorkScope to be active", childWorkScope.isActive)
        assertTrue("Expected launched job to be active", job.isActive)

        // Cancel the parent scope immediately (no waiting for children)
        parent.cancel(awaitChildrenComplete = false)

        // Advance test scheduler to process cancellation
        advanceUntilIdle()

        // Now, everything should be inactive
        assertFalse("Parent should be inactive", parent.isActive)
        assertFalse("Child scope should be inactive", child.isActive)
        assertFalse("ChildWork scope should be inactive", childWorkScope.isActive)
        assertFalse("Job should be cancelled", job.isActive)
    }

    /**
     * 2) If the parent is already inactive at creation time, the child is immediately cancelled.
     */
    @Test
    fun `childManagedScope is cancelled if parent is already inactive`() = runTest {
        val parent = ManagedCoroutineScope(backgroundScope)
        // Cancel the parent first
        parent.cancel(awaitChildrenComplete = false, message = "Initial cancel")
        advanceUntilIdle()

        // Attempt to create a child after the parent is inactive
        val child = parent.childManagedScope("ChildB")
        assertFalse("Child scope should be inactive if parent was already cancelled", child.isActive)

        // Any scope created from this child should also be inactive
        val childWorkScope = child.create("ChildWork")
        assertFalse("ChildWork scope is also inactive", childWorkScope.isActive)
    }

    /**
     * 3) cancel with awaitChildrenComplete = true defers the parent's cancellation
     * until all children complete.
     */
    @Test
    fun `cancel awaits children completion`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val parent = ManagedCoroutineScope(backgroundScope)

        val child = parent.childManagedScope("ChildC", dispatcher)
        val childWorkScope = child.create("ChildWork", dispatcher)

        // Launch a job that takes some simulated time
        var didFinish = false
        val job = childWorkScope.launch {
            delay(2)
            didFinish = true
        }

        // Now request parent cancellation but wait for children
        parent.cancel(awaitChildrenComplete = true, message = "Awaiting child")

        // The parent is still active until the child is done
        advanceTimeBy(1)

        assertTrue(job.isActive)
        assertTrue("Parent should still be active, because child isn't cancelled yet", parent.isActive)
        assertTrue("Child should be active", child.isActive)
        assertFalse("Child's job hasn't finished yet", didFinish)

        // Complete the child's delay
        advanceUntilIdle()

        // Now child finishes
        assertFalse(job.isActive)
        assertTrue("Job must be done", didFinish)
        assertTrue("Parent should still be active, because child isn't cancelled yet", parent.isActive)
        assertTrue("Child should be active", child.isActive)

        child.cancel(awaitChildrenComplete = true, message = "Awaiting child")
        runCurrent()

        // The parent's wait condition is satisfied, so parent becomes inactive
        assertFalse("Child is also inactive once completed", child.isActive)
        assertFalse("Parent is now inactive after child finished", parent.isActive)
    }

    /**
     * 4) Multiple children with staggered completion. The parent defers its own cancellation
     * until all children explicitly cancel or complete their jobs.
     */
    @Test
    fun `multiple children with staggered completion`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val parent = ManagedCoroutineScope(backgroundScope)

        // Create two child managed scopes
        val fastChild = parent.childManagedScope("FastChild", dispatcher)
        val slowChild = parent.childManagedScope("SlowChild", dispatcher)

        // Create "work scopes" under each child – they'll do some delayed tasks
        val fastWork = fastChild.create("FastWork", dispatcher)
        val slowWork = slowChild.create("SlowWork", dispatcher)

        // Launch jobs that "finish" in some simulated time – but
        // that doesn't finalize the child scope, it just ends the child's coroutines.
        var fastDone = false
        var slowDone = false
        fastWork.launch {
            delay(2)      // some small "work"
            fastDone = true
        }
        slowWork.launch {
            delay(5)      // a slightly longer "work"
            slowDone = true
        }

        // Now the parent requests cancellation, but it awaits child scopes
        parent.cancel(awaitChildrenComplete = true, message = "Await all children scopes")

        // Advance time to let the child's coroutines finish – but
        // finishing childWork doesn't automatically cancel the child scope itself.
        advanceTimeBy(5)
        advanceUntilIdle()

        assertTrue("Fast child's work is done", fastDone)
        assertTrue("Slow child's work is done", slowDone)
        // However, the children are still active unless we explicitly cancel them
        assertTrue("fastChild scope is still active", fastChild.isActive)
        assertTrue("slowChild scope is still active", slowChild.isActive)
        // Parent is awaiting children
        assertTrue("Parent is still active, awaiting child scopes to finalize", parent.isActive)

        // Simulate the child scopes deciding they're done:
        fastChild.cancel(awaitChildrenComplete = false, "FastChild finished")
        runCurrent()
        assertFalse("FastChild is now inactive", fastChild.isActive)
        // But slowChild is still active
        assertTrue("SlowChild is still active", slowChild.isActive)
        // Parent remains active because one child remains
        assertTrue("Parent is still active, waiting on slowChild", parent.isActive)

        // Finally, slowChild is also cancelled or calls finish
        slowChild.cancel(awaitChildrenComplete = false, "SlowChild done")
        runCurrent()

        // Now all children have been cancelled
        assertFalse("SlowChild is now inactive", slowChild.isActive)
        // The parent can finalize cancellation
        assertFalse("Parent is inactive after all children are done", parent.isActive)
    }

    /**
     * 5) Subsequent cancel calls can do an immediate cancel only if we're not already
     * locked into "await children" mode and haven't fully cancelled yet.
     */
    @Test
    fun `subsequent cancel calls do not override 'awaitChildrenComplete' if still waiting`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val parent = ManagedCoroutineScope(backgroundScope)

        // Create a child scope that remains active until explicitly cancelled
        val child = parent.childManagedScope("RepeatChild", dispatcher)
        assertTrue("Child should be active initially", child.isActive)

        // 1) First call: await children. Since the child is active, set isAwaitingChildrenComplete
        parent.cancel(awaitChildrenComplete = true, message = "Wait for child")
        runCurrent()

        // Parent is now "locked in" to waiting for children
        assertTrue("Parent is still active, deferring cancellation until child is done", parent.isActive)

        // 2) Second call with await
        parent.cancel(awaitChildrenComplete = true, message = "Second call wait")
        runCurrent()

        // Because the parent is already waiting, this second call does nothing
        assertTrue(
            "Parent remains active, ignoring the second call, because it was already awaiting children",
            parent.isActive
        )
        assertTrue("Child is still active", child.isActive)

        // 3) If the child is explicitly cancelled or finishes, the parent can finalize its cancellation
        child.cancel(awaitChildrenComplete = false, message = "Child done")
        runCurrent()

        // Now the child is inactive and no children remain, so the parent finalizes cancellation
        assertFalse("Child is now inactive", child.isActive)
        assertFalse("Parent is also inactive", parent.isActive)

        // 4) A final call does nothing, because the parent is already inactive
        parent.cancel(awaitChildrenComplete = false, message = "No-op")
        runCurrent()
        assertFalse("Parent remains inactive", parent.isActive)
    }
}