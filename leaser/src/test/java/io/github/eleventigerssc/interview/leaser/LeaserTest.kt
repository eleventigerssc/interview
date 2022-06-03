package io.github.eleventigerssc.interview.leaser

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import kotlin.random.Random
import org.junit.Test

private const val DEFAULT_TEST_TIMEOUT_MS = 10_000L

class LeaserTest {

    private val countingLong = AtomicLong()

    @Test(timeout = DEFAULT_TEST_TIMEOUT_MS)
    fun `exclusive, acquire one after other, use and release`() {
        val leaser = Leaser.Factory.get().exclusiveFor {
            countingLong.getAndIncrement()
        }
        leaser.acquire().use { lease ->
            assertEquals(0, lease.value)
        }
        leaser.acquire().use { lease ->
            assertEquals(1, lease.value)
        }
    }

    @Test(timeout = DEFAULT_TEST_TIMEOUT_MS)
    fun `exclusive, acquire one, attempt to acquire another, blocks until first is released`() {
        val leaser = Leaser.Factory.get().exclusiveFor {
            countingLong.getAndIncrement()
        }

        leaser.acquire().use {
            val leaseB = leaser.tryAcquireWithTimeout(1L, TimeUnit.MILLISECONDS)
            assertNull(leaseB)
            assertEquals(1, countingLong.get())
        }

        assertNotNull(leaser.acquire())
        assertEquals(2, countingLong.get())
    }

    @Test(timeout = DEFAULT_TEST_TIMEOUT_MS)
    fun `refCounted, acquire one, acquire another, provide the same value`() {
        val leaser = Leaser.Factory.get().refCountedFor {
            countingLong.getAndIncrement()
        }

        leaser.acquire().use { leaseA ->
            leaser.acquire().use { leaseB ->
                assertEquals(0, leaseA.value)
                assertEquals(0, leaseB.value)
                assertEquals(1, countingLong.get())
            }
        }
    }

    @Test(timeout = DEFAULT_TEST_TIMEOUT_MS)
    fun `refCounted, concurrent acquire, provide the same value until released`() {
        val leaser = Leaser.Factory.get().refCountedFor {
            countingLong.getAndIncrement()
        }
        val concurrentLeaseCount = 64
        val executor = Executors.newScheduledThreadPool(concurrentLeaseCount)
        try {
            val leasesAcquired = CountDownLatch(concurrentLeaseCount)
            val leasesClosed = CountDownLatch(concurrentLeaseCount)
            (1..concurrentLeaseCount).forEach { _ ->
                executor.execute {
                    leaser.acquire().use { lease ->
                        assertEquals(0, lease.value)
                        leasesAcquired.countDown()
                        val sleepMillis = (Random.nextFloat() * 100).toLong()
                        TimeUnit.MILLISECONDS.sleep(sleepMillis)
                    }
                    leasesAcquired.await()
                    leasesClosed.countDown()
                }
            }
            leasesClosed.await()
            leaser.acquire().use { lease ->
                assertEquals(1, lease.value)
            }
        } finally {
            executor.shutdown()
        }
    }
}
