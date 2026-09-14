package com.hydroping.app.domain

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CharacterStateMachineTest {

    private lateinit var stateMachine: CharacterStateMachine

    @Before
    fun setup() {
        stateMachine = CharacterStateMachine()
    }

    @Test
    fun `test recently celebrated returns HAPPY_CELEBRATING mood`() {
        val mood = stateMachine.determineMood(
            lastDrinkTimeMs = System.currentTimeMillis(),
            currentTotalMl = 1000,
            targetDailyMl = 2000,
            quietStartHour = 23,
            quietEndHour = 7,
            isDrinkingNow = false,
            isRecentlyCelebrated = true
        )
        assertEquals(CharacterMood.HAPPY_CELEBRATING, mood)
    }

    @Test
    fun `test drinking in progress returns DRINKING_IN_PROGRESS mood`() {
        val mood = stateMachine.determineMood(
            lastDrinkTimeMs = System.currentTimeMillis(),
            currentTotalMl = 1000,
            targetDailyMl = 2000,
            quietStartHour = 23,
            quietEndHour = 7,
            isDrinkingNow = true,
            isRecentlyCelebrated = false
        )
        assertEquals(CharacterMood.DRINKING_IN_PROGRESS, mood)
    }

    @Test
    fun `test goal completed returns HYDRATED mood`() {
        val mood = stateMachine.determineMood(
            lastDrinkTimeMs = System.currentTimeMillis() - (5 * 60 * 60 * 1000), // 5 hrs ago
            currentTotalMl = 2500,
            targetDailyMl = 2000,
            quietStartHour = 0, // not quiet right now unless mid night
            quietEndHour = 0,
            isDrinkingNow = false,
            isRecentlyCelebrated = false
        )
        assertEquals(CharacterMood.HYDRATED, mood)
    }

    @Test
    fun `test recent drink within 45 mins returns HYDRATED mood`() {
        val now = System.currentTimeMillis()
        val recentDrinkMs = now - (20 * 60 * 1000) // 20 mins ago

        val mood = stateMachine.determineMood(
            lastDrinkTimeMs = recentDrinkMs,
            currentTotalMl = 500,
            targetDailyMl = 2000,
            quietStartHour = 0,
            quietEndHour = 0
        )
        assertEquals(CharacterMood.HYDRATED, mood)
    }
}
