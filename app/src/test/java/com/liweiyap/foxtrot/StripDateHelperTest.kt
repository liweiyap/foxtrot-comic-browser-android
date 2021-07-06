package com.liweiyap.foxtrot

import com.liweiyap.foxtrot.util.StripDate
import com.liweiyap.foxtrot.util.StripDateHelper
import org.junit.Assert.*
import org.junit.Test

class StripDateHelperTest {
    @Test
    fun testLeapYears() {
        // evenly divisible by 100 but not by 400
        assertFalse(StripDateHelper.isLeapYear(2100))
        assertFalse(StripDateHelper.isLeapYear(2200))
        assertFalse(StripDateHelper.isLeapYear(2300))
        assertFalse(StripDateHelper.isLeapYear(2500))
        assertFalse(StripDateHelper.isLeapYear(2600))

        // evenly divisible by both 100 and 400
        assertTrue(StripDateHelper.isLeapYear(2000))
        assertTrue(StripDateHelper.isLeapYear(2400))

        assertTrue(StripDateHelper.isLeapYear(2020))
        assertFalse(StripDateHelper.isLeapYear(2021))

        assertThrows(Exception::class.java) { StripDateHelper.isLeapYear(-1) }
        assertThrows(Exception::class.java) { StripDateHelper.isLeapYear(1900) }
        assertThrows(Exception::class.java) { StripDateHelper.isLeapYear(1987) }

        assertTrue(StripDateHelper.isLeapYear(1988))
    }

    @Test
    fun testDaysInYear() {
        // evenly divisible by 100 but not by 400
        assertEquals(StripDateHelper.countDaysInYear(2100), 365)
        assertEquals(StripDateHelper.countDaysInYear(2200), 365)
        assertEquals(StripDateHelper.countDaysInYear(2300), 365)
        assertEquals(StripDateHelper.countDaysInYear(2500), 365)
        assertEquals(StripDateHelper.countDaysInYear(2600), 365)

        // evenly divisible by both 100 and 400
        assertEquals(StripDateHelper.countDaysInYear(2000), 366)
        assertEquals(StripDateHelper.countDaysInYear(2400), 366)

        assertEquals(StripDateHelper.countDaysInYear(2020), 366)
        assertEquals(StripDateHelper.countDaysInYear(2021), 365)
        assertEquals(StripDateHelper.countDaysInYear(1988), 366)
    }

    @Test
    fun testDaysInMonth() {
        assertEquals(StripDateHelper.countDaysInMonth(1, 2020), 31)
        assertEquals(StripDateHelper.countDaysInMonth(1, 2021), 31)

        assertEquals(StripDateHelper.countDaysInMonth(2, 2020), 29)
        assertEquals(StripDateHelper.countDaysInMonth(2, 2021), 28)

        assertEquals(StripDateHelper.countDaysInMonth(3, 2020), 31)
        assertEquals(StripDateHelper.countDaysInMonth(3, 2021), 31)

        assertEquals(StripDateHelper.countDaysInMonth(4, 2020), 30)
        assertEquals(StripDateHelper.countDaysInMonth(4, 2021), 30)

        assertEquals(StripDateHelper.countDaysInMonth(5, 2020), 31)
        assertEquals(StripDateHelper.countDaysInMonth(5, 2021), 31)

        assertEquals(StripDateHelper.countDaysInMonth(6, 2020), 30)
        assertEquals(StripDateHelper.countDaysInMonth(6, 2021), 30)

        assertEquals(StripDateHelper.countDaysInMonth(7, 2020), 31)
        assertEquals(StripDateHelper.countDaysInMonth(7, 2021), 31)

        assertEquals(StripDateHelper.countDaysInMonth(8, 2020), 31)
        assertEquals(StripDateHelper.countDaysInMonth(8, 2021), 31)

        assertEquals(StripDateHelper.countDaysInMonth(9, 2020), 30)
        assertEquals(StripDateHelper.countDaysInMonth(9, 2021), 30)

        assertEquals(StripDateHelper.countDaysInMonth(10, 2020), 31)
        assertEquals(StripDateHelper.countDaysInMonth(10, 2021), 31)

        assertEquals(StripDateHelper.countDaysInMonth(11, 2020), 30)
        assertEquals(StripDateHelper.countDaysInMonth(11, 2021), 30)

        assertEquals(StripDateHelper.countDaysInMonth(12, 2020), 31)
        assertEquals(StripDateHelper.countDaysInMonth(12, 2021), 31)

        assertThrows(Exception::class.java) { StripDateHelper.countDaysInMonth(0, 2020) }
        assertThrows(Exception::class.java) { StripDateHelper.countDaysInMonth(13, 2020) }
    }

    @Test
    fun testReferenceDate() {
        assertEquals(StripDateHelper.countDaysFromReferenceDate(StripDate(1, 1, 1988)), 0)
        assertEquals(StripDateHelper.countDaysFromReferenceDate(StripDate(2, 1, 1988)), 1)
        assertEquals(StripDateHelper.countDaysFromReferenceDate(StripDate(13, 10, 2021)), 12339)
    }
}