package com.liweiyap.foxtrot

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class InitOncePropertyTest {
    @Test
    fun readPrematureExpectedFailure() {
        val data = property  // Value is not already initialized, so, as expected, exception is thrown
    }

    @Test
    fun writeTwiceExpectedFailure() {
        property = "Test1"
        property = "Test2"  // Value is already initialized, so, as expected, exception is thrown
    }

    @Test
    fun writeAndReadExpectedSuccess() {
        property = "Test"
        val data1 = property
        val data2 = property  // As expected, no exception is thrown
    }

    var property: String by initOnce()
}