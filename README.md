# Set - Native Android Card Game

A custom native Android implementation of the classic pattern-matching card game **Set**, built with modern Kotlin and Jetpack Compose.

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)

## ✨ Features

- **Mathematical Engine**: Complete 81-card Set universe with exhaustive combinatorial validation, 3rd card solver, and set detection.
- **Procedural Canvas Rendering**: Clean vector-drawn shapes (Ovals, Diamonds, and Bézier-curved Squiggles) with Solid, Open, and clipped Striped hatching.
- **Adaptive Layout**: Responsive 3x4 grid that fits phone screens without vertical scrolling, with smooth expansion for 15+ cards.
- **Game Modes**:
  - **Classic**: Full 81-card speedrun tracking elapsed time, sets found, and remaining deck.
  - **Practice & Zen**: Untimed gameplay with error diagnostics explaining why selected cards do or do not form a Set.
  - **Set Trainer**: Quick drills to train third-card pattern recognition.
- **Multi-Tiered Hints**:
  1. Set count announcement
  2. Single-card pulsing highlight
  3. Two-card highlight (leaving you to find the third)
- **Assists & Accessibility**:
  - High-contrast / Colorblind Mode (Orange, Teal, Indigo palette)
  - Auto-deal when 0 sets exist on board
  - Optional on-board sets counter
- **Audio & Haptic Feedback**: Tactile vibrations and synthesized major-chord chimes via `AudioTrack`.

## 📱 How to Run

### Prerequisites
- Android Studio Ladybug or newer
- Android SDK (API 24+)
- An Android device with USB Debugging enabled (or Android emulator)

### Command Line
```powershell
# Build and install directly to a connected device or running emulator:
.\gradlew.bat installDebug
```

### Android Studio
Open the project in Android Studio and click **Run** (`Shift + F10`).
