package com.hydroping.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HydrationUnitTest {

    @Test
    fun `test format ML returns whole number with ml label`() {
        val formatted = HydrationUnitHelper.format(750, HydrationUnit.ML)
        assertEquals("750 ml", formatted)

        val shortFormatted = HydrationUnitHelper.formatShort(750, HydrationUnit.ML)
        assertEquals("750", shortFormatted)
    }

    @Test
    fun `test format L returns two decimal places with L label`() {
        val formatted = HydrationUnitHelper.format(1500, HydrationUnit.L)
        assertEquals("1.50 L", formatted)

        val formatted2 = HydrationUnitHelper.format(2000, HydrationUnit.L)
        assertEquals("2.00 L", formatted2)
    }

    @Test
    fun `test format OZ converts and formats accurately`() {
        // 1000 ml / 29.5735 =~ 33.8 oz
        val formatted = HydrationUnitHelper.format(1000, HydrationUnit.OZ)
        assertTrue(formatted.endsWith("oz"))
        assertTrue(formatted.startsWith("33.") || formatted.startsWith("34."))
    }

    @Test
    fun `test toMl conversion for each unit`() {
        assertEquals(500, HydrationUnitHelper.toMl(500f, HydrationUnit.ML))
        assertEquals(2000, HydrationUnitHelper.toMl(2.0f, HydrationUnit.L))
        val ozToMl = HydrationUnitHelper.toMl(10f, HydrationUnit.OZ)
        assertTrue("10 oz should be approximately 295ml", ozToMl in 290..300)
    }

    @Test
    fun `test fromMl conversion roundtrip`() {
        val ml = 2500
        val l = HydrationUnitHelper.fromMl(ml, HydrationUnit.L)
        assertEquals(2.5f, l, 0.01f)

        val convertedBack = HydrationUnitHelper.toMl(l, HydrationUnit.L)
        assertEquals(ml, convertedBack)
    }
}
