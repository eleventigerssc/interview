package io.github.eleventigerssc.interview.leaser

import java.io.Closeable
import java.util.ServiceLoader
import java.util.concurrent.TimeUnit

/**
 * Provides access to a value of type [T] that requires access control due to resource, time or other constraints.
 * Once a caller acquires a [Lease], the caller can obtain a value through the [Lease.value] property and interact with
 * it until [Lease.close] is called.
 */
interface Leaser<T> {

    /**
     * Attempts to acquire a [Lease] blocking indefinitely.
     */
    @Throws(InterruptedException::class)
    fun acquire(): Lease<T> {
        return tryAcquireWithTimeout(Long.MAX_VALUE, TimeUnit.DAYS)
                ?: throw IllegalStateException("Failed to acquire a lease")
    }

    /**
     * Attempts to acquire a [Lease] with the provided [timeout] duration of some [timeUnit].
     * @return [Lease] or null if the provided [timeout] duration expired.
     */
    @Throws(InterruptedException::class)
    fun tryAcquireWithTimeout(timeout: Long, timeUnit: TimeUnit): Lease<T>?

    /**
     * Provides limited access to a [value] of type [T] until [close] is called.
     */
    interface Lease<T> : Closeable {

        val value: T
    }

    /**
     * Provides a way to create instances of [Leaser] that have different access control semantics.
     */
    interface Factory {

        /**
         * Creates a [Leaser] that allows only a single [Lease] access to a value obtained from the [provider].
         * The [provider] is called each time a new [Lease] is acquired.
         */
        fun <T> exclusiveFor(provider: () -> T): Leaser<T>

        /**
         * Creates a [Leaser] that allows multiple [Lease]s access to a value obtained from the [provider].
         * The [provider] is called for a first [Lease] acquired but not any other until all [Lease] instances are
         * closed.
         */
        fun <T> refCountedFor(provider: () -> T): Leaser<T>

        companion object {

            /**
             * Attempts to load a [Factory] implementation using the [ServiceLoader] framework.
             *
             * To make it discoverable, [Leaser.Factory] implementation class can be manually declared in the resources
             * under the `META-INF/services/io.github.eleventigerssc.interview.leaser.Leaser$Factory` file or annotated
             * with the [com.google.auto.service.AutoService] annotation for the resource file to be generated
             * automatically.
             */
            @JvmStatic
            fun get(): Factory {
                val iterator = ServiceLoader.load(Factory::class.java).iterator()
                return if (iterator.hasNext()) {
                    iterator.next()
                } else {
                    throw NoSuchElementException("Could not find an implementation of the Leaser.Factory")
                }
            }
        }
    }
}
