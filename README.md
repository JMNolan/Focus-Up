# Focus-Up 🎯

An Android mobile app created to help users focus for set periods of time to maximize productivity. Complete focus sessions to earn fun stickers for your collection!

## Features ✨

- **Timer Selection**: Choose from multiple timer durations:
  - 5 seconds (for testing)
  - 15 minutes
  - 30 minutes
  - 1 hour
  - 2 hours

- **Full-Screen Timer**: Immersive countdown timer with circular progress indicator
- **Sticker Rewards**: Earn a random sticker after completing each focus session
- **Sticker Book**: View and manage your growing collection of earned stickers
- **Modern UI**: Beautiful Material Design 3 interface built with Jetpack Compose

## Architecture 🏗️

This project follows **Clean Architecture** principles with a **modular structure**:

### Module Structure

```
Focus-Up/
├── app/                          # Main application module
│   └── navigation/               # Navigation setup
├── core/
│   ├── domain/                   # Business logic & models
│   │   ├── model/               # Domain models (Sticker, TimerDuration)
│   │   └── repository/          # Repository interfaces
│   ├── data/                     # Data layer
│   │   ├── local/               # Room database
│   │   ├── repository/          # Repository implementations
│   │   └── di/                  # Dependency injection
│   └── ui/                       # Shared UI components & theme
│       └── theme/               # Material Design 3 theme
└── feature/
    ├── timer/                    # Timer feature module
    │   ├── TimerViewModel       # Timer business logic
    │   └── TimerScreen          # Timer UI
    └── stickerbook/              # Sticker book feature module
        ├── StickerBookViewModel # Sticker collection logic
        └── StickerBookScreen    # Sticker book UI
```

### Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Database**: Room
- **Async**: Kotlin Coroutines & Flow
- **Navigation**: Jetpack Navigation Compose

## Getting Started 🚀

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17 or later
- Android SDK (minSdk 24, targetSdk 34)

### Building the Project

1. Clone the repository:
```bash
git clone https://github.com/yourusername/Focus-Up.git
cd Focus-Up
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run the app on an emulator or physical device

### Running the App

1. Select a timer duration from the dropdown menu
2. Press the START button to begin your focus session
3. The timer will run in fullscreen mode
4. When complete, you'll receive a congratulatory message and a random sticker
5. View your sticker collection by tapping "View Sticker Book"

## Project Structure Details 📁

### Core Modules

**core:domain**
- Contains business logic and domain models
- No Android dependencies
- Defines repository interfaces

**core:data**
- Implements repository interfaces
- Room database for local storage
- Hilt modules for dependency injection

**core:ui**
- Shared Compose components
- Material Design 3 theme
- Common UI utilities

### Feature Modules

**feature:timer**
- Timer selection screen
- Running timer with progress indicator
- Completion screen with sticker reward
- Timer state management with ViewModel

**feature:stickerbook**
- Grid display of earned stickers
- Empty state for new users
- Sticker details (emoji, name, date earned)

## Dependencies 📦

Key dependencies include:
- `androidx.compose:compose-bom:2024.01.00` - Jetpack Compose
- `androidx.room:room-ktx:2.6.1` - Room Database
- `com.google.dagger:hilt-android:2.50` - Dependency Injection
- `androidx.navigation:navigation-compose:2.7.6` - Navigation
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0` - ViewModel

## Contributing 🤝

Contributions are welcome! Please feel free to submit a Pull Request.

## License 📄

This project is open source and available under the MIT License.

## Future Enhancements 💡

- [ ] Add notification when timer completes
- [ ] Background timer support
- [ ] Statistics and analytics
- [ ] Custom timer durations
- [ ] Themes and customization
- [ ] Sound effects and haptic feedback
- [ ] Share stickers with friends
- [ ] Daily/weekly challenges
