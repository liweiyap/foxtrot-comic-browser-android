package com.liweiyap.foxtrot

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.text.DateFormatSymbols

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DateFormatterTest {

    private fun testSince(rawDate: String, expectedDay: Int, expectedMonth: Int, expectedYear: Int) {
        val date: StripDate = DateFormatter.formatDateSinceApi26(rawDate)
        assertDate(date, expectedDay, expectedMonth, expectedYear)
    }

    private fun testPre(rawDate: String, expectedDay: Int, expectedMonth: Int, expectedYear: Int) {
        val date: StripDate = DateFormatter.formatDatePreApi26(rawDate)
        assertDate(date, expectedDay, expectedMonth, expectedYear)
    }

    private fun assertDate(date: StripDate, expectedDay: Int, expectedMonth: Int, expectedYear: Int) {
        assertEquals(date.day, expectedDay)
        assertEquals(date.month, expectedMonth)
        assertEquals(date.year, expectedYear)
    }

    // https://stackoverflow.com/questions/1038570/how-can-i-convert-an-integer-to-localized-month-name-in-java
    private fun getMonthName(month: Int): String? {
        return DateFormatSymbols().months[month - 1]
    }

    @Test fun testDate0Since() = testSince("Published June 27, 2021", 27, 6, 2021)
    @Test fun testDate0Pre() = testPre("Published June 27, 2021", 27, 6, 2021)

    @Test fun testDate1Since() = testSince("Published June 3, 2021", 3, 6, 2021)
    @Test fun testDate1Pre() = testPre("Published June 3, 2021", 3, 6, 2021)

    @Test fun testDate2Since() = testSince("Published December 27, 2020", 27, 12, 2020)
    @Test fun testDate2Pre() = testPre("Published December 27, 2020", 27, 12, 2020)

    @Test fun testDate3Since() = testSince("Published February 29, 2032", 29, 2, 2032)
    @Test fun testDate3Pre() = testPre("Published February 29, 2032", 29, 2, 2032)

    @Test fun testDate4Since() = testSince("Published May 30, 2021", 30, 5, 2021)
    @Test fun testDate4Pre() = testPre("Published May 30, 2021", 30, 5, 2021)

    @Test fun testDate5Since() = testSince("Published April 11, 2021", 11, 4, 2021)
    @Test fun testDate5Pre() = testPre("Published April 11, 2021", 11, 4, 2021)

    @Test fun testDate6Since() = testSince("Published March 7, 2021", 7, 3, 2021)
    @Test fun testDate6Pre() = testPre("Published March 7, 2021", 7, 3, 2021)

    @Test fun testDate7Since() = testSince("Published January 31, 2021", 31, 1, 2021)
    @Test fun testDate7Pre() = testPre("Published January 31, 2021", 31, 1, 2021)

    @Test fun testDate8Since() = testSince("Published November 1, 2020", 1, 11, 2020)
    @Test fun testDate8Pre() = testPre("Published November 1, 2020", 1, 11, 2020)

    @Test fun testDate9Since() = testSince("Published October 18, 2020", 18, 10, 2020)
    @Test fun testDate9Pre() = testPre("Published October 18, 2020", 18, 10, 2020)

    @Test fun testDate10Since() = testSince("Published September 6, 2020", 6, 9, 2020)
    @Test fun testDate10Pre() = testPre("Published September 6, 2020", 6, 9, 2020)

    @Test fun testDate11Since() = testSince("Published August 23, 2020", 23, 8, 2020)
    @Test fun testDate11Pre() = testPre("Published August 23, 2020", 23, 8, 2020)

    @Test fun testDate12Since() = testSince("Published July 12, 2020", 12, 7, 2020)
    @Test fun testDate12Pre() = testPre("Published July 12, 2020", 12, 7, 2020)
}