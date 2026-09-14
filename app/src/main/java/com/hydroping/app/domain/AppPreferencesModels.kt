package com.hydroping.app.domain

enum class ThemeMode(val title: String) {
    SYSTEM("System Default"),
    DARK("Dark Theme"),
    LIGHT("Light Theme")
}

enum class NotificationStyle(
    val title: String,
    val description: String,
    val iconEmoji: String = "💬",
    val sampleQuote: String = ""
) {
    FRIENDLY(
        title = "Friendly Character",
        description = "Warm & uplifting dialogues from your chosen character",
        iconEmoji = "🤗",
        sampleQuote = "Time for a refreshing sip! You're doing awesome ✨"
    ),
    MOTIVATIONAL(
        title = "Motivational",
        description = "High-energy hydration and health boosts",
        iconEmoji = "🔥",
        sampleQuote = "Crush your day! Stay hydrated and power through! 🚀"
    ),
    PROGRESS_BASED(
        title = "Progress Tracker",
        description = "Shows remaining ml and % toward your goal",
        iconEmoji = "📊",
        sampleQuote = "1,250 / 2,500 ml logged (50% done). Keep the streak alive! 💧"
    ),
    MINIMAL(
        title = "Minimal Direct",
        description = "Clean & concise reminders without extras",
        iconEmoji = "🎯",
        sampleQuote = "HydraPing reminder: Time to drink 250 ml of water."
    ),
    PERSONALIZED(
        title = "Smart Dynamic",
        description = "Adaptive reminders addressing you with live context",
        iconEmoji = "⚡",
        sampleQuote = "Hey Manoj, high energy output detected! Stay hydrated ⚡"
    )
}

enum class ReminderSound(
    val title: String,
    val iconEmoji: String = "🔔",
    val description: String = ""
) {
    CHIME("Crystal Chime", "🔔", "Harmonic bell chime"),
    GENTLE_DROP("Water Drop", "💧", "Hydrodynamic liquid plop"),
    PING("Radar Ping", "📡", "Cyber sonar radar ping"),
    BUBBLE("Bubble Pop", "🫧", "Crisp bubbly liquid pop"),
    SILENT("Silent / Vibration", "🔕", "Vibration only (silent)")
}

enum class ReminderAnimation(
    val title: String,
    val description: String,
    val iconEmoji: String
) {
    LIQUID_WAVE("Liquid Wave", "Fluid wave sloshing with dynamic water level", "🌊"),
    RIPPLE_PULSE("Rippling Pulse", "Concentric water ripples radiating outward", "🫧"),
    GLOWING_ORB("Cyber Orb", "Futuristic neon electric energy core with sparks", "🔮")
}

enum class GoalStrategy(val title: String, val description: String) {
    MANUAL("Manual Goal", "Custom fixed daily goal set by you"),
    APP_SUGGESTED("App Suggested", "Calculated based on your weight/activity baseline"),
    DYNAMIC("Dynamic (Activity + Weather)", "Adapts with workout sessions and hot weather")
}

enum class ActivityLevel(val simpleTitle: String, val subtitle: String, val extraMl: Int) {
    SEDENTARY("Low", "Desk work / resting", 0),
    MODERATE("Normal", "Daily walk / light active", 300),
    ACTIVE("High", "Workout / gym / sports", 600),
    VERY_ACTIVE("Athlete", "Intense training / heavy sweat", 900);

    val title: String get() = "$simpleTitle ($subtitle)"
}

data class ScheduleProfile(
    val weekdayStartHour: Int = 7,
    val weekdayStartMinute: Int = 0,
    val weekdayEndHour: Int = 23,
    val weekdayEndMinute: Int = 0,
    val weekendStartHour: Int = 8,
    val weekendStartMinute: Int = 0,
    val weekendEndHour: Int = 23,
    val weekendEndMinute: Int = 0,
    val weekdayIntervalMins: Int = 60,
    val weekendIntervalMins: Int = 90,
    val studyIntervalMins: Int = 45,
    val workModeEnabled: Boolean = false,
    val studyModeEnabled: Boolean = false,
    val exerciseModeEnabled: Boolean = false,
    val weatherBoosterEnabled: Boolean = false
)
