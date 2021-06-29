package com.liweiyap.foxtrot

import com.liweiyap.foxtrot.util.initOnce
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class InitOncePropertyTest {
    @Test
    fun run() {
        assertEquals(readPrematureExpectedFailure(), false)
        assertEquals(writeTwiceExpectedFailure(), false)
        assertEquals(writeAndReadExpectedSuccess(), true)
    }

    private fun readPrematureExpectedFailure(): Boolean {
        try {
            val data = property0  // Value is not already initialized, so, as expected, exception is thrown
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun writeTwiceExpectedFailure(): Boolean {
        try {
            property1 = "Test1"
            property1 = "Test2"  // Value is already initialized, so, as expected, exception is thrown
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun writeAndReadExpectedSuccess(): Boolean {
        try {
            property2 = "Test"
            val data1 = property2
            val data2 = property2  // As expected, no exception is thrown
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private var property0: String by initOnce()
    private var property1: String by initOnce()
    private var property2: String by initOnce()
}