# Focus-Up App Workflow

## Screen Flow

### 1. Main Screen (ContentView)
The main screen displays:
- "Focus Up" title at the top
- "Select your focus duration" subtitle
- Dropdown menu (Picker) with timer options:
  - Select a timer (placeholder)
  - 5 Seconds (Testing)
  - 15 Minutes
  - 30 Minutes
  - 1 Hour
  - 2 Hours
- "Start Focus Session" button at the bottom
  - DISABLED (gray) when no timer is selected
  - ENABLED (blue) when a timer is selected

### 2. Timer Screen (TimerView)
When the user taps "Start Focus Session":
- Full-screen black background
- "Stay Focused" title
- Large countdown timer display (HH:MM:SS or MM:SS format)
- Red "Cancel" button to exit early
- Timer counts down from selected duration to 00:00

### 3. Completion Screen (Part of TimerView)
When timer reaches 00:00:
- Full-screen display with:
  - "Focus Complete!" message
  - Large emoji sticker (randomly selected from 30 options)
  - "You earned a sticker!" message
  - Blue "Done" button to return to main screen

## Key Features Implementation

### Timer Selection
- Uses SwiftUI Picker with MenuPickerStyle
- State management with @State for selectedDuration
- Button enabled/disabled based on selection

### Countdown Logic
- Timer.scheduledTimer with 1-second intervals
- Updates timeRemaining state each second
- Automatically triggers completion when reaching 0

### Sticker Reward System
- 30 unique emoji stickers in StickerBook
- Random selection using randomElement()
- Animated presentation when timer completes

### Full-Screen Presentation
- Uses .fullScreenCover modifier
- Binding to control presentation state
- Proper cleanup on dismiss

## User Experience Flow

1. User opens app → sees main screen
2. User taps dropdown → sees timer options
3. User selects duration → button becomes enabled
4. User taps "Start" → full-screen timer appears
5. User waits (or cancels) → timer counts down
6. Timer completes → sticker reward appears
7. User taps "Done" → returns to main screen
8. User can start another session
