# 💧 HydraPing — Smart Cyber-Dark Hydration Companion

[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20%28API%2026--35%29-blue.svg)](https://android.com)
[![Language](https://img.shields.io/badge/Kotlin-2.0.20-purple.svg)](https://kotlinlang.org)
[![UI Toolkit](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.06.00-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVI%2FMVVM-brightgreen.svg)]()
[![Database](https://img.shields.io/badge/Storage-Room%20SQLite%20%2B%20DataStore-orange.svg)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**HydraPing** is a high-performance, offline-first smart hydration companion for Android built entirely with **Jetpack Compose**, **Kotlin Coroutines & Flows**, **Room SQLite**, and a custom **Cyber-Dark & Ocean Glow** design system.

---

## ✨ Key Features

### ⚡ 1. Animated Mascot Companions & Multi-Mood Faces
Choose your favorite hydration companion:
- ⚡ **Pikachu**: High-voltage Pokémon companion charging up daily streaks.
- 🐭 **Jerry**: Cheeky mouse from *Tom & Jerry* outsmarting dehydration.
- 🐾 **Whiskers**: Calm, stylish, and clever feline companion.

Each character adapts dynamically across **4 hydration mood states**:
- 🥵 **Thirsty / Behind Target**: Droopy, exhausted face with urgent reminders to hydrate.
- 😊 **Happy / Normal Progress**: Cheerful smiling face praising steady pacing.
- 🏆 **Celebrating**: Victory celebration with golden trophies & confetti at 100% completion.
- 😴 **Sleeping**: Peaceful resting moon & stars face during quiet sleeping hours.

---

### 🌊 2. Dashboard Dynamic Character Animation Styles
Select from 3 fluid real-time background liquid animations behind your mascot:
- 🌊 **Liquid Wave**: Fluid wave sloshing with dynamic water level.
- 🫧 **Rippling Pulse**: Concentric ripples radiating outward.
- 🔮 **Cyber Orb**: Futuristic neon electric energy core with sparks.

---

### 🔔 3. Smart Adaptive Reminders & Rich Heads-Up Notifications
- **Adaptive Pacing Engine**: Dynamically calculates reminder intervals and catch-up volumes based on your intake velocity and active hours.
- **Zero-Drag Native Notifications**: Full message, character dialogue, and progress summary (`Today: 1,250 / 2,500 ml`) are immediately visible on heads-up display without requiring swipe/drag expansion.
- **Action Buttons**: Instant `Drink 250 ml`, `Log Water`, `Snooze`, and `Skip` directly from the notification shade.

---

### 🔊 4. Real-Time Procedural Sound Effects
Zero-latency 16-bit PCM audio synthesizer (`AudioTrack`) with instant sound previews:
- 🔔 **Crystal Chime**: Harmonic multi-frequency bell chord.
- 💧 **Water Drop**: Liquid hydrodynamic drop plop.
- 📡 **Radar Ping**: Cyber sonar ping with acoustic decay.
- 🫧 **Bubble Pop**: Bubbly liquid pop.
- 🔕 **Silent / Vibration**: Haptic vibration feedback.

---

### ☀️ 5. Real-Time Weather Booster (Open-Meteo API)
- Connects to the keyless Open-Meteo weather API to fetch live temperature and weather conditions.
- Automatically calculates dynamic hydration boosts ($+200\text{ ml}$ warm, $+350\text{ ml}$ hot, $+500\text{ ml}$ extreme heat) with a live weather badge on the Home Dashboard.

---

### 📊 6. Performance Trends & Personal Records
- **Dashboard Trends**: On-track pacing %, average sip volume, total drinks today, active streak, and peak drinking hours.
- **Personal Records**: All-time milestones (Best Day Intake, Longest Streak, Total Lifetime Volume, Total Drinks Logged).

---

### ⚙️ 7. Full Customization & Privacy-First Offline Architecture
- **Custom Inputs**: Set any exact daily target (ml), custom reminder interval (mins), and custom snooze duration.
- **Flexible Schedule**: Independent weekday vs weekend wake & sleep time pickers.
- **100% Offline-First**: All data stays private on-device in Room SQLite with JSON/CSV backup & restore.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0.20
- **Min SDK**: API 26 (Android 8.0 Oreo) • **Target SDK**: API 35 (Android 15)
- **UI Framework**: Jetpack Compose with Material 3 & Custom Cyber Design Tokens
- **State Management**: Kotlin StateFlow & SharedFlow (MVI/MVVM)
- **Persistence**: Room Database (SQLite) + Jetpack DataStore Preferences
- **Widgets**: Jetpack Glance App Widget (`HydroCompanionWidget`)
- **Audio**: Procedural 16-bit PCM audio synthesis via `AudioTrack`
- **Weather API**: Open-Meteo REST API (free, keyless)
- **Testing**: JUnit 4 with mockable timestamp engines (30/30 unit tests passing)

---

## 🚀 Building & Running from Source

### Prerequisites
- JDK 17+
- Android SDK (API 35, Build-Tools 35.0.1)

### Build Debug APK
```bash
# Clone the repository
git clone https://github.com/<your-username>/HydraPing.git
cd HydraPing

# Build debug APK & run unit tests
./gradlew testDebugUnitTest assembleDebug
```

The compiled APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📂 Project Structure

```
HydraPing/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/hydroping/app/
│   │   │   │   ├── data/            # Room Database, DAOs, WeatherService, Preferences
│   │   │   │   ├── domain/          # AdaptiveReminderEngine, DialogueEngine, Models
│   │   │   │   ├── reminder/        # ReminderReceiver, QuickDrinkReceiver, NotificationHelper
│   │   │   │   ├── ui/              # Jetpack Compose Screens, ViewModels, Theme, Components
│   │   │   │   ├── utils/           # SoundEffectHelper, BackupRestoreHelper, CsvExport
│   │   │   │   └── widget/          # Glance Home Screen Widget
│   │   │   └── res/                 # Vector drawables, Mascot 3D assets, XML configs
│   │   └── test/                    # Unit test suite (AdaptiveReminderEngineTest, etc.)
│   └── build.gradle.kts
├── gradle/wrapper/                  # Official Gradle Wrapper binaries & properties
├── build.gradle.kts                 # Root project build script
├── settings.gradle.kts              # Project settings & repositories
├── gradle.properties                # Build environment & JVM optimizations
└── README.md
```

---

## 📄 License
This project is licensed under the [MIT License](LICENSE).
