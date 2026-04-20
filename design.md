# UI/UX Alignment Report & Design Instructions

The background images for Sterling's World have been updated to high-fidelity artwork. However, the current Jetpack Compose button layouts are hardcoded to generic screen positions and do not align with the "hotspots" (signs, gates, and interactable elements) drawn into the new art.

## Objective
Update the `Modifier` properties for buttons on the following screens to ensure they overlap perfectly with their intended visual targets in the background artwork.

---

## 1. Welcome Screen (`WelcomeScreen.kt`)
- **Background:** `bg_welcome_entrance.jpg`
- **Current Issue:** Buttons are stacked in the bottom center with generic padding.
- **Goal:** 
    - Move the **"Enter"** button down to sit directly on top of the physical turnstiles/gate entrance shown in the bottom-center of the artwork.
    - Move the **"Start Myelin Protocol"** button to be slightly above the "Enter" button, appearing as if it's a sign hanging near the entrance.
- **Instruction:** Use `Modifier.align(Alignment.BottomCenter)` but increase `padding(bottom)` or use `Modifier.offset(y = ...)` to hit the turnstile line.

## 2. AOL Screen (`AolScreen.kt`)
- **Background:** `bg_aol.jpg`
- **Current Issue:** The "Play" button is floating in a generic bottom-center container.
- **Goal:** 
    - Align the **"Play"** button with the glowing circular portal/archway in the center of the image.
    - The button should feel like the "Trigger" for entering that archway.
- **Instruction:** Use `Modifier.align(Alignment.Center)` and apply a vertical offset to center it within the archway's opening.

## 3. Grand Arcade Indoor (`GrandArcadeIndoorScreen.kt`)
- **Background:** `bg_grand_arcade_indoor.jpg`
- **Current Issue:** The vertical list of games is a straight column that ignores the perspective of the arcade floor and machines.
- **Goal:** 
    - Shift the game buttons so they feel like they are floating in the "aisle" between the arcade machines.
    - Adjust the width of the buttons to match the perspective (wider at the bottom, narrower at the top).
- **Instruction:** Wrap the game list in a `Column` with `horizontalAlignment = Alignment.CenterHorizontally`, and consider using `graphicsLayer { rotationX = ... }` or varying widths for a pseudo-perspective effect.

## 4. Symptom Striker (`SymptomStrikerScreen.kt`)
- **Background:** `bg_symptom_striker.jpg`
- **Current Issue:** The artwork has a literal **"ENTER GAUNTLET"** button drawn at the bottom. The UI "Play" button is currently floating above it.
- **Goal:** 
    - Match the UI **"Play"** button's size and position exactly to the "ENTER GAUNTLET" button in the artwork.
    - Make the UI button's background transparent or a subtle glow so the artwork's button shows through.
- **Instruction:** Target the bottom 15% of the screen. Increase `padding(bottom)` to approx `80.dp` to align with the illustrated button.

## 5. Cognitive Creamery (`CreameryScreen.kt`)
- **Background:** `bg_cognitive_creamery.png`
- **Current Issue:** The artwork has a brown **"Start Quest"** sign in the center.
- **Goal:** 
    - Position the UI **"Play"** button directly over the illustrated "Start Quest" sign.
- **Instruction:** Use `Modifier.align(Alignment.Center)` and a small `Modifier.offset(y = 50.dp)` to land on the sign.

## 6. General Instructions for all Screens
- **Responsive Layout:** Use `BoxWithConstraints` where necessary to ensure that "hotspot" alignment holds up on different aspect ratios (e.g., 16:9 vs 21:9).
- **Interactive Feedback:** Ensure `Modifier.clickable` remains highly responsive even when using offsets.
- **Visual Style:** Use `Color.Transparent` for button backgrounds if the artwork already provides a high-quality button graphic underneath.
