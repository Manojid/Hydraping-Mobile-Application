package com.hydroping.app.domain

import androidx.annotation.DrawableRes
import com.hydroping.app.R

enum class CharacterMood {
    SLEEPING,
    HYDRATED,
    WAITING,
    A_LITTLE_THIRSTY,
    THIRSTY,
    VERY_THIRSTY,
    DRINKING_IN_PROGRESS,
    HAPPY_CELEBRATING
}

enum class PersonalityType {
    CUTE,
    SARCASTIC,
    MINIMAL
}

data class CharacterProfile(
    val id: String,
    val name: String,
    val description: String,
    val defaultPersonality: PersonalityType,
    @DrawableRes val previewRes: Int
)

object CharacterCatalog {
    val Pikachu = CharacterProfile(
        id = "pikachu",
        name = "Pikachu",
        description = "⚡ Energetic electric Pokémon character charging up your hydration!",
        defaultPersonality = PersonalityType.CUTE,
        previewRes = R.drawable.pikachu_happy
    )

    val Jerry = CharacterProfile(
        id = "jerry",
        name = "Jerry",
        description = "🐭 Cheeky & clever mouse from Tom & Jerry outsmarting thirst!",
        defaultPersonality = PersonalityType.CUTE,
        previewRes = R.drawable.jerry_happy
    )

    val Whiskers = CharacterProfile(
        id = "whiskers",
        name = "Whiskers",
        description = "😼 Stylish & clever cat demanding high hydration standards.",
        defaultPersonality = PersonalityType.SARCASTIC,
        previewRes = R.drawable.whiskers_happy
    )

    val allCharacters = listOf(Pikachu, Jerry, Whiskers)

    fun fromId(id: String): CharacterProfile =
        allCharacters.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: Pikachu

    @DrawableRes
    fun getDrawable(characterId: String, mood: CharacterMood): Int {
        return when (characterId.lowercase()) {
            "jerry" -> when (mood) {
                CharacterMood.SLEEPING -> R.drawable.jerry_sleeping
                CharacterMood.THIRSTY, CharacterMood.VERY_THIRSTY, CharacterMood.A_LITTLE_THIRSTY -> R.drawable.jerry_thirsty
                CharacterMood.HAPPY_CELEBRATING -> R.drawable.jerry_celebrating
                else -> R.drawable.jerry_happy
            }
            "whiskers" -> when (mood) {
                CharacterMood.SLEEPING -> R.drawable.whiskers_sleeping
                CharacterMood.THIRSTY, CharacterMood.VERY_THIRSTY, CharacterMood.A_LITTLE_THIRSTY -> R.drawable.whiskers_thirsty
                CharacterMood.HAPPY_CELEBRATING -> R.drawable.whiskers_celebrating
                else -> R.drawable.whiskers_happy
            }
            else -> when (mood) {
                CharacterMood.SLEEPING -> R.drawable.pikachu_sleeping
                CharacterMood.THIRSTY, CharacterMood.VERY_THIRSTY, CharacterMood.A_LITTLE_THIRSTY -> R.drawable.pikachu_thirsty
                CharacterMood.HAPPY_CELEBRATING -> R.drawable.pikachu_celebrating
                else -> R.drawable.pikachu_happy
            }
        }
    }
}
