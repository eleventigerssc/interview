package io.github.eleventigerssc.interview.leaser

import java.lang.Long.max
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

    @Test
    fun `exclusive, no lease acquired, provider not called`() {
        Leaser.Factory.get().exclusiveFor {
            countingLong.getAndIncrement()
        }

        assertEquals(0, countingLong.get())
    }

    @Test
    fun `refCounted, no lease acquired, provider not called`() {
        Leaser.Factory.get().refCountedFor {
            countingLong.getAndIncrement()
        }

        assertEquals(0, countingLong.get())
    }

    @Test(timeout = DEFAULT_TEST_TIMEOUT_MS)
    fun `exclusive, acquire one after other, use and release`() {
        val leaser = Leaser.Factory.get().exclusiveFor {
            countingLong.getAndIncrement()
        }
        leaser.acquire().use { lease ->
            assertEquals(0, lease.value)
            assertEquals(0, lease.value)
        }
        leaser.acquire().use { lease ->
            assertEquals(1, lease.value)
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
    fun `exclusive, acquire one, attempt to acquire another, blocks until interrupted`() {
        val leaser = Leaser.Factory.get().exclusiveFor {
            countingLong.getAndIncrement()
        }

        val leaseA = leaser.acquire()
        val executor = Executors.newSingleThreadExecutor()
        try {
            val future = executor.submit {
                try {
                    leaser.acquire()
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    leaseA.close()
                }
            }

            TimeUnit.MILLISECONDS.sleep(1L)
            future.cancel(true)

            assertNotNull(leaser.acquire())
            assertEquals(2, countingLong.get())
        } finally {
            executor.shutdown()
        }
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
            val futures = (1..concurrentLeaseCount).map {
                executor.submit {
                    TimeUnit.NANOSECONDS.sleep(max(1L, (Random.nextFloat() * 100).toLong()))
                    leaser.acquire().use { lease ->
                        assertEquals(0, lease.value)
                        leasesAcquired.countDown()
                        leasesAcquired.await()
                    }
                }
            }
            futures.forEach { it.get() }
            leaser.acquire().use { lease ->
                assertEquals(1, lease.value)
            }
        } finally {
            executor.shutdown()
        }
    }
}
