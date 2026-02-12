# Focus-Up

An iOS app created to help users focus for a set period of time to maximize productivity. Complete focus sessions and earn fun stickers as rewards!

## Features

- **Timer Selection**: Choose from multiple focus durations:
  - 5 seconds (for testing)
  - 15 minutes
  - 30 minutes
  - 1 hour
  - 2 hours

- **Countdown Timer**: Full-screen countdown display to help you stay focused
- **Sticker Rewards**: Earn a random sticker from the sticker book upon completing a focus session
- **Simple Interface**: Easy-to-use dropdown menu and start button

## How to Use

1. Launch the app
2. Select a timer duration from the dropdown menu
3. Tap the "Start Focus Session" button (enabled only after selecting a duration)
4. Stay focused while the countdown timer runs in full-screen mode
5. When the timer completes, you'll receive a random sticker as a reward!
6. Tap "Done" to return to the main screen and start another session

## Building and Running

This is an iOS app built with SwiftUI. To run it:

1. Open `FocusUp.xcodeproj` in Xcode
2. Select your target device or simulator
3. Press Cmd+R to build and run

**Requirements:**
- Xcode 15.0 or later
- iOS 16.0 or later
- macOS for development

## Project Structure

```
FocusUp/
├── FocusUpApp.swift      # Main app entry point
├── ContentView.swift     # Main screen with timer selection
├── TimerView.swift       # Full-screen countdown timer
├── StickerBook.swift     # Sticker collection and random selection
└── Assets.xcassets/      # App assets and icons
```

## Sticker Collection

The app includes 30 different stickers (emoji rewards) that users can earn by completing focus sessions. Each completion awards a random sticker from the collection.
