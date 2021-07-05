package com.liweiyap.foxtrot

import com.liweiyap.foxtrot.util.StripDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class StripDateTest {
    @Test
    fun run() {
        assertNotEquals(StripDate(25, 6, 2021), StripDate(26, 6, 2021))
        assertEquals(StripDate(25, 6, 2021), StripDate(25, 6, 2021))
    }
}