package com.hydroping.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MilestoneServiceTest {

    @Test
    fun `test 1-day streak unlocks no multi-day badges`() {
        val badges = MilestoneService.getMilestones(1)
        assertEquals(4, badges.size)
        assertFalse(badges.first { it.id == "flame_3" }.isUnlocked)
        assertFalse(badges.first { it.id == "silver_7" }.isUnlocked)
        assertFalse(badges.first { it.id == "gold_14" }.isUnlocked)
        assertFalse(badges.first { it.id == "trophy_30" }.isUnlocked)
    }

    @Test
    fun `test 3-day streak unlocks 3-day flame badge`() {
        val badges = MilestoneService.getMilestones(3)
        assertTrue(badges.first { it.id == "flame_3" }.isUnlocked)
        assertFalse(badges.first { it.id == "silver_7" }.isUnlocked)
    }

    @Test
    fun `test 7-day streak unlocks flame and silver wave`() {
        val badges = MilestoneService.getMilestones(7)
        assertTrue(badges.first { it.id == "flame_3" }.isUnlocked)
        assertTrue(badges.first { it.id == "silver_7" }.isUnlocked)
        assertFalse(badges.first { it.id == "gold_14" }.isUnlocked)
    }

    @Test
    fun `test 30-day streak unlocks all badges`() {
        val badges = MilestoneService.getMilestones(30)
        assertTrue(badges.all { it.isUnlocked })
    }
}
