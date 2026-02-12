# Focus-Up App Screenshots & UI Description

Since this is a code-only implementation without actual screenshots, here's a detailed description of what each screen looks like:

## 1. Main Screen (Launch View)

```
┌─────────────────────────────────┐
│                                 │
│                                 │
│                                 │
│          Focus Up               │
│     (Large, Bold Title)         │
│                                 │
│   Select your focus duration    │
│       (Gray subtitle)           │
│                                 │
│   ┌───────────────────────┐    │
│   │  Select a timer    ▼  │    │
│   └───────────────────────┘    │
│     (Dropdown Menu)             │
│                                 │
│                                 │
│                                 │
│                                 │
│                                 │
│   ┌───────────────────────┐    │
│   │ Start Focus Session   │    │
│   │      (Gray/Blue)      │    │
│   └───────────────────────┘    │
│        (Bottom Button)          │
│                                 │
└─────────────────────────────────┘
```

### UI Elements:
- Title: "Focus Up" - Large, bold, centered
- Subtitle: "Select your focus duration" - Smaller, gray, centered
- Picker: Dropdown menu with light gray background, rounded corners
  - Options: Select a timer, 5 Seconds (Testing), 15 Minutes, 30 Minutes, 1 Hour, 2 Hours
- Button: Full-width button with padding, rounded corners
  - Gray when disabled (no selection)
  - Blue when enabled (timer selected)

## 2. Timer Screen (Countdown)

```
┌─────────────────────────────────┐
│                                 │
│   ███████████████████████████   │
│   ███  BLACK BACKGROUND  ███   │
│   ███████████████████████████   │
│                                 │
│        Stay Focused             │
│       (White Title)             │
│                                 │
│                                 │
│          00:14:23               │
│   (Huge White Numbers - 80pt)   │
│                                 │
│                                 │
│                                 │
│      ┌───────────────┐          │
│      │    Cancel     │          │
│      │     (Red)     │          │
│      └───────────────┘          │
│                                 │
│   ███████████████████████████   │
└─────────────────────────────────┘
```

### UI Elements:
- Background: Full-screen black
- Title: "Stay Focused" - White, medium size
- Timer: Large countdown display in white
  - Format: HH:MM:SS (for 1+ hours) or MM:SS (less than 1 hour)
  - Font: System rounded, 80pt, bold
- Cancel Button: Red background, white text, centered

## 3. Completion Screen (Sticker Reward)

```
┌─────────────────────────────────┐
│                                 │
│   ███████████████████████████   │
│   ███  BLACK BACKGROUND  ███   │
│   ███████████████████████████   │
│                                 │
│      Focus Complete!            │
│    (Large White Title)          │
│                                 │
│                                 │
│            🌟                   │
│     (Huge Emoji - 100pt)        │
│                                 │
│                                 │
│   You earned a sticker!         │
│      (White Text)               │
│                                 │
│      ┌───────────────┐          │
│      │     Done      │          │
│      │    (Blue)     │          │
│      └───────────────┘          │
│                                 │
│   ███████████████████████████   │
└─────────────────────────────────┘
```

### UI Elements:
- Background: Full-screen black
- Title: "Focus Complete!" - Large, white, bold
- Sticker: Random emoji from collection, 100pt size
- Message: "You earned a sticker!" - White, medium size
- Done Button: Blue background, white text, centered

## Color Scheme

- Primary: Blue (#007AFF - iOS default blue)
- Destructive: Red
- Disabled: Gray
- Background (Timer): Black
- Text (on black): White
- Text (on white): Primary or gray

## Interactions

1. **Dropdown Selection**: 
   - Tap opens menu with all timer options
   - Selection updates button state

2. **Start Button**:
   - Disabled state: Gray, no tap feedback
   - Enabled state: Blue, tappable
   - Tap: Full-screen transition to timer

3. **Timer Cancel**:
   - Red button dismisses timer screen
   - Returns to main screen

4. **Done Button**:
   - Dismisses reward screen
   - Returns to main screen for new session
