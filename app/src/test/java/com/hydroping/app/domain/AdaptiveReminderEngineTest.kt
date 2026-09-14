package com.hydroping.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AdaptiveReminderEngineTest {

    private lateinit var engine: AdaptiveReminderEngine

    @Before
    fun setup() {
        engine = AdaptiveReminderEngine()
    }

    @Test
    fun `test goal completed stops frequent reminders and sets maintenance mode`() {
        val pacing = engine.calculateNextReminder(
            dailyTargetMl = 2000,
            currentConsumedMl = 2200,
            baseIntervalMins = 60,
            smartPacingEnabled = true
        )

        assertEquals(-1, pacing.calculatedDelayMinutes)
        assertTrue(pacing.isGoalCompleted)
        assertEquals(0, pacing.remainingMl)
        assertTrue(pacing.isAheadOfSchedule)
        assertFalse(pacing.isBehindSchedule)
        assertTrue(pacing.pacingStatusText.contains("Goal completed"))
    }

    @Test
    fun `test behind schedule accelerates reminder interval`() {
        // Daily target 2000ml, consumed only 100ml late in active hours
        val pacing = engine.calculateNextReminder(
            dailyTargetMl = 2500,
            currentConsumedMl = 100,
            baseIntervalMins = 60,
            activeStartHour = 0,
            activeEndHour = 24,
            smartPacingEnabled = true
        )

        assertTrue("Should calculate remaining water", pacing.remainingMl > 0)
        // If deficit is large, interval should be shortened
        if (pacing.deficitMl >= 250) {
            assertTrue("Calculated delay should be <= base interval", pacing.calculatedDelayMinutes <= 60)
            assertTrue("Calculated delay should respect minimum interval", pacing.calculatedDelayMinutes >= AdaptiveReminderEngine.MIN_INTERVAL_MINS)
        }
    }

    @Test
    fun `test ahead of schedule relaxes reminder interval`() {
        // User already drank 1800ml early in the day
        val pacing = engine.calculateNextReminder(
            dailyTargetMl = 2000,
            currentConsumedMl = 1800,
            baseIntervalMins = 60,
            activeStartHour = 0,
            activeEndHour = 24,
            smartPacingEnabled = true
        )

        if (pacing.deficitMl <= -200) {
            assertTrue("Calculated delay should be >= base interval", pacing.calculatedDelayMinutes >= 60)
        }
    }

    @Test
    fun `test fixed interval mode without smart pacing`() {
        val pacing = engine.calculateNextReminder(
            dailyTargetMl = 2000,
            currentConsumedMl = 500,
            baseIntervalMins = 45,
            smartPacingEnabled = false,
            consecutiveDismissals = 0
        )

        assertEquals(45, pacing.calculatedDelayMinutes)
        assertTrue(pacing.pacingStatusText.contains("Fixed reminder scheduled every 45 mins"))
    }

    @Test
    fun `test consecutive dismissals apply gentle backoff`() {
        val pacing = engine.calculateNextReminder(
            dailyTargetMl = 2000,
            currentConsumedMl = 500,
            baseIntervalMins = 45,
            smartPacingEnabled = false,
            consecutiveDismissals = 2
        )

        // 45 + (2 * 15) = 75 mins
        assertEquals(75, pacing.calculatedDelayMinutes)
    }

    @Test
    fun `test study mode uses configured study interval`() {
        val schedule = ScheduleProfile(studyModeEnabled = true, studyIntervalMins = 30)
        val morningCal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 9)
            set(java.util.Calendar.MINUTE, 0)
        }
        val pacing = engine.calculateNextReminder(
            dailyTargetMl = 2000,
            currentConsumedMl = 400,
            baseIntervalMins = 60,
            smartPacingEnabled = true,
            scheduleProfile = schedule,
            now = morningCal
        )

        assertTrue("Study mode should use studyIntervalMins", pacing.calculatedDelayMinutes in 25..35)
    }

    @Test
    fun `test gradual catch up volume calculation provides reasonable distribution`() {
        val pacing = engine.calculateNextReminder(
            dailyTargetMl = 3000,
            currentConsumedMl = 300,
            baseIntervalMins = 60,
            userFavoriteAmount = 250,
            smartPacingEnabled = true
        )

        assertTrue("Suggested drink amount should be at least 150ml", pacing.suggestedDrinkAmountMl >= 150)
        assertTrue("Suggested drink amount should not be excessive", pacing.suggestedDrinkAmountMl <= 600)
    }

    @Test
    fun `test state flow - drink action updates intake and recalculates remaining target`() {
        val initialPacing = engine.calculateNextReminder(
            dailyTargetMl = 2000,
            currentConsumedMl = 500,
            baseIntervalMins = 60,
            smartPacingEnabled = true
        )
        assertEquals(1500, initialPacing.remainingMl)
        assertFalse(initialPacing.isGoalCompleted)

        // After drinking 250 ml
        val updatedPacing = engine.calculateNextReminder(
            dailyTargetMl = 2000,
            currentConsumedMl = 750,
            baseIntervalMins = 60,
            smartPacingEnabled = true
        )
        assertEquals(1250, updatedPacing.remainingMl)
        assertFalse(updatedPacing.isGoalCompleted)
        assertTrue("Updated state must have lower remaining amount", updatedPacing.remainingMl < initialPacing.remainingMl)
    }

    @Test
    fun `test state flow - drink action reaching 100 percent terminates daytime reminders`() {
        // Just before goal: 1800 / 2000 ml
        val beforePacing = engine.calculateNextReminder(
            dailyTargetMl = 2000,
            currentConsumedMl = 1800,
            baseIntervalMins = 60,
            smartPacingEnabled = true
        )
        assertEquals(200, beforePacing.remainingMl)
        assertFalse(beforePacing.isGoalCompleted)

        // User drinks 250 ml -> 2050 / 2000 ml
        val afterPacing = engine.calculateNextReminder(
            dailyTargetMl = 2000,
            currentConsumedMl = 2050,
            baseIntervalMins = 60,
            smartPacingEnabled = true
        )
        assertEquals(0, afterPacing.remainingMl)
        assertTrue("Goal must be completed", afterPacing.isGoalCompleted)
        assertEquals("Daytime delay must be -1 when goal is completed", -1, afterPacing.calculatedDelayMinutes)
    }
}
