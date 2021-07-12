package com.liweiyap.foxtrot

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.liweiyap.foxtrot.scraper.DateFormatter
import com.liweiyap.foxtrot.util.StripDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DateFormatterTest {

    private fun testSinceExpectedSuccess(rawDate: String, expectedDay: Int, expectedMonth: Int, expectedYear: Int) {
        val date: StripDate = DateFormatter.formatDateSinceApi26(rawDate)
            ?: throw Exception("DateFormatterTest::testSince(): error retrieving date of strip.")
        assertEquals(date, StripDate(expectedDay, expectedMonth, expectedYear))
    }

    private fun testPreExpectedSuccess(rawDate: String, expectedDay: Int, expectedMonth: Int, expectedYear: Int) {
        val date: StripDate = DateFormatter.formatDatePreApi26(rawDate)
            ?: throw Exception("DateFormatterTest::testPre(): error retrieving date of strip.")
        assertEquals(date, StripDate(expectedDay, expectedMonth, expectedYear))
    }

    private fun testSinceExpectedFailure(rawDate: String) {
        assertNull(DateFormatter.formatDateSinceApi26(rawDate))
    }

    private fun testPreExpectedFailure(rawDate: String) {
        assertNull(DateFormatter.formatDatePreApi26(rawDate))
    }

    @Test fun testDate0Since() = testSinceExpectedSuccess("Published June 27, 2021", 27, 6, 2021)
    @Test fun testDate0Pre() = testPreExpectedSuccess("Published June 27, 2021", 27, 6, 2021)

    @Test fun testDate1Since() = testSinceExpectedSuccess("Published June 3, 2021", 3, 6, 2021)
    @Test fun testDate1Pre() = testPreExpectedSuccess("Published June 3, 2021", 3, 6, 2021)

    @Test fun testDate2Since() = testSinceExpectedSuccess("Published December 27, 2020", 27, 12, 2020)
    @Test fun testDate2Pre() = testPreExpectedSuccess("Published December 27, 2020", 27, 12, 2020)

    @Test fun testDate3Since() = testSinceExpectedSuccess("Published February 29, 2032", 29, 2, 2032)
    @Test fun testDate3Pre() = testPreExpectedSuccess("Published February 29, 2032", 29, 2, 2032)

    @Test fun testDate4Since() = testSinceExpectedSuccess("Published May 30, 2021", 30, 5, 2021)
    @Test fun testDate4Pre() = testPreExpectedSuccess("Published May 30, 2021", 30, 5, 2021)

    @Test fun testDate5Since() = testSinceExpectedSuccess("Published April 11, 2021", 11, 4, 2021)
    @Test fun testDate5Pre() = testPreExpectedSuccess("Published April 11, 2021", 11, 4, 2021)

    @Test fun testDate6Since() = testSinceExpectedSuccess("Published March 7, 2021", 7, 3, 2021)
    @Test fun testDate6Pre() = testPreExpectedSuccess("Published March 7, 2021", 7, 3, 2021)

    @Test fun testDate7Since() = testSinceExpectedSuccess("Published January 31, 2021", 31, 1, 2021)
    @Test fun testDate7Pre() = testPreExpectedSuccess("Published January 31, 2021", 31, 1, 2021)

    @Test fun testDate8Since() = testSinceExpectedSuccess("Published November 1, 2020", 1, 11, 2020)
    @Test fun testDate8Pre() = testPreExpectedSuccess("Published November 1, 2020", 1, 11, 2020)

    @Test fun testDate9Since() = testSinceExpectedSuccess("Published October 18, 2020", 18, 10, 2020)
    @Test fun testDate9Pre() = testPreExpectedSuccess("Published October 18, 2020", 18, 10, 2020)

    @Test fun testDate10Since() = testSinceExpectedSuccess("Published September 6, 2020", 6, 9, 2020)
    @Test fun testDate10Pre() = testPreExpectedSuccess("Published September 6, 2020", 6, 9, 2020)

    @Test fun testDate11Since() = testSinceExpectedSuccess("Published August 23, 2020", 23, 8, 2020)
    @Test fun testDate11Pre() = testPreExpectedSuccess("Published August 23, 2020", 23, 8, 2020)

    @Test fun testDate12Since() = testSinceExpectedSuccess("Published July 12, 2020", 12, 7, 2020)
    @Test fun testDate12Pre() = testPreExpectedSuccess("Published July 12, 2020", 12, 7, 2020)

    @Test fun testDate13Since() = testSinceExpectedFailure("June 27, 2021")
    @Test fun testDate13Pre() = testPreExpectedFailure("June 27, 2021")

    @Test fun testDate14Since() = testSinceExpectedFailure("27/06/2021")
    @Test fun testDate14Pre() = testPreExpectedFailure("27/06/2021")

    @Test fun testDate15Since() = testSinceExpectedFailure("27.06.2021")
    @Test fun testDate15Pre() = testPreExpectedFailure("27.06.2021")

    @Test fun testDate16Since() = testSinceExpectedFailure("27-06-2021")
    @Test fun testDate16Pre() = testPreExpectedFailure("27-06-2021")
}