package com.liweiyap.foxtrot

import com.liweiyap.foxtrot.util.StripDate
import com.liweiyap.foxtrot.util.StripDateHelper
import org.junit.Assert.*
import org.junit.Test
import java.security.spec.InvalidParameterSpecException

class StripDateTest {
    @Test
    fun testDateEquality() {
        assertNotEquals(StripDate(25, 6, 2021), StripDate(26, 6, 2021))
        assertEquals(StripDate(25, 6, 2021), StripDate(25, 6, 2021))
        assertFalse(StripDate(25, 6, 2021) > StripDate(26, 6, 2021))
        assertTrue(StripDate(25, 6, 2021) < StripDate(26, 6, 2021))
        assertEquals(StripDate(25, 6, 2021) - StripDate(26, 6, 2021), -1)

        assertEquals(StripDate(1, 3, 2020) - StripDate(28, 2, 2020), 2)
        assertEquals(StripDate(1, 2, 2004) - StripDate(1, 2, 2000), 1461)
    }

    /**
     * https://sqa.stackexchange.com/questions/7814/what-are-good-approaches-to-test-dates
     */
    @Test
    fun testDateValidity() {
        assertThrows(Exception::class.java) { StripDate(0, 1, 2021) }
        assertThrows(Exception::class.java) { StripDate(32, 1, 2021) }
        assertThrows(Exception::class.java) { StripDate(30, 2, 2020) }
        assertThrows(Exception::class.java) { StripDate(31, 2, 2020) }
        assertThrows(Exception::class.java) { StripDate(29, 2, 2021) }
        assertThrows(Exception::class.java) { StripDate(30, 2, 2020) }
        assertThrows(Exception::class.java) { StripDate(31, 2, 2020) }
        assertThrows(Exception::class.java) { StripDate(31, 4, 2020) }
        assertThrows(Exception::class.java) { StripDate(31, 6, 2020) }
        assertThrows(Exception::class.java) { StripDate(31, 9, 2020) }
        assertThrows(Exception::class.java) { StripDate(31, 11, 2020) }
        assertThrows(Exception::class.java) { StripDate(31, 12, 1987) }
        assertThrows(Exception::class.java) { StripDate(31, 12, 0) }
        assertThrows(Exception::class.java) { StripDate(31, 0, 1988) }

        assertTrue(StripDateHelper.isValidDate(StripDate(29, 2, 2020)))
    }

    @Test
    fun testDayOfWeek() {
        val dayOfWeek: (StripDate) -> String = { date: StripDate ->
            when (date.getDayOfWeek()) {
                0 -> "Mon"
                1 -> "Tue"
                2 -> "Wed"
                3 -> "Thu"
                4 -> "Fri"
                5 -> "Sat"
                6 -> "Sun"
                else -> throw InvalidParameterSpecException()
            }
        }

        assertEquals(dayOfWeek(StripDate(30, 8, 2010)), "Mon")
        assertEquals(dayOfWeek(StripDate(4, 7, 2021)), "Sun")
    }
}