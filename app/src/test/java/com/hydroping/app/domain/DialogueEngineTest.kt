package com.hydroping.app.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DialogueEngineTest {

    @Test
    fun `test personalized greeting with user name Alex`() {
        val prompt = DialogueEngine.getNotificationPrompt(
            userName = "Alex",
            characterId = "pikachu",
            amountMl = 250,
            isBehindTarget = false
        )
        assertTrue("Prompt should address Alex", prompt.contains("Alex"))
        assertTrue("Prompt should specify 250 ml", prompt.contains("250"))
    }

    @Test
    fun `test behind target catch up notification prompt`() {
        val prompt = DialogueEngine.getNotificationPrompt(
            userName = "Alex",
            characterId = "jerry",
            amountMl = 300,
            isBehindTarget = true,
            remainingMl = 800
        )
        assertTrue("Prompt should address Alex", prompt.contains("Alex"))
        assertTrue("Prompt should mention tired or Jerry or drink", prompt.contains("Jerry", ignoreCase = true) || prompt.contains("tired", ignoreCase = true))
    }

    @Test
    fun `test goal completed celebratory dialogue contains name`() {
        val dialogue = DialogueEngine.getDialogue(
            userName = "Samantha",
            characterId = "pikachu",
            personality = PersonalityType.CUTE,
            mood = CharacterMood.HYDRATED,
            percentProgress = 100,
            remainingMl = 0
        )
        assertTrue("Dialogue should include Samantha", dialogue.contains("Samantha"))
        assertTrue("Dialogue should celebrate goal completion", dialogue.contains("crushed") || dialogue.contains("goal") || dialogue.contains("100%"))
    }

    @Test
    fun `test whiskers personality tone`() {
        val whiskersDialogue = DialogueEngine.getDialogue(
            userName = "Jordan",
            characterId = "whiskers",
            personality = PersonalityType.CUTE,
            mood = CharacterMood.A_LITTLE_THIRSTY,
            percentProgress = 30
        )
        assertTrue(whiskersDialogue.contains("Jordan"))
        assertTrue(whiskersDialogue.contains("patience") || whiskersDialogue.contains("Water") || whiskersDialogue.contains("🐾"))
    }

    @Test
    fun `test pikachu tired thirsty dialogue when behind target`() {
        val dialogue = DialogueEngine.getDialogue(
            userName = "Ash",
            characterId = "pikachu",
            personality = PersonalityType.CUTE,
            mood = CharacterMood.THIRSTY,
            percentProgress = 20,
            isBehindTarget = true,
            deficitMl = 350
        )
        assertTrue("Pikachu dialogue should include Ash", dialogue.contains("Ash"))
        assertTrue("Pikachu dialogue should indicate tiredness or low energy", dialogue.contains("Energy") || dialogue.contains("tired") || dialogue.contains("power"))
    }

    @Test
    fun `test jerry Tom and Jerry character dialogue and prompt`() {
        val dialogue = DialogueEngine.getDialogue(
            userName = "Tom",
            characterId = "jerry",
            personality = PersonalityType.CUTE,
            mood = CharacterMood.HYDRATED,
            percentProgress = 80
        )
        assertTrue("Jerry dialogue should include Tom", dialogue.contains("Tom"))
        assertTrue("Jerry dialogue should reflect smooth progress", dialogue.contains("progress") || dialogue.contains("🐭"))

        val prompt = DialogueEngine.getNotificationPrompt(
            userName = "Tom",
            characterId = "jerry",
            amountMl = 250
        )
        assertTrue(prompt.contains("Tom"))
        assertTrue(prompt.contains("Jerry") || prompt.contains("refreshing"))
    }

    @Test
    fun `test non-shaming missed day recovery dialogue`() {
        val recovery = DialogueEngine.getMissedDayRecoveryDialogue(userName = "Alex", characterId = "jerry")
        assertTrue(recovery.contains("Alex"))
        assertTrue("Should be encouraging and non-shaming", recovery.contains("past") || recovery.contains("sip"))
    }
}
