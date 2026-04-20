# Implementation Brief: Cognitive Creamery "Juice" Pass

**Goal:** Elevate the Cognitive Creamery focus suite from a standard UI to a tactile, high-atmosphere "midnight parlor" experience.

## 1. Environmental Atmosphere
- **Background Layer:** Use `Image(painterResource(R.drawable.bg_cognitive_creamery))` as the foundation.
- **Dynamic Theming:** Apply a dark `Box` overlay with 40% opacity to ensure the UI cards remain readable against the complex parlor artwork.
- **Parlor Ambience:** Trigger a looping low-volume ambient track (if sound system exists) and snappy "scoop/clink" sounds on successful taps.

## 2. Feedback & Animation (The "Juice")
- **Scale Feedback:** Apply a `graphicsLayer { scaleX = ...; scaleY = ... }` animation to all game buttons (`Sequence` and `Clarity`) that triggers a brief 1.1x scale-up on tap.
- **Success Sparkle:** When a round is cleared, trigger a brief particle-burst effect (using `Canvas` or simple `AnimatedVisibility` icons) at the center of the card.
- **Fatigue Vignette:** As `fatigueLevel` increases, apply a `Brush.radialGradient` overlay to the screen edges that shifts from transparent to a "frosty" light blue, visually representing the "Brain Freeze" creeping in.

## 3. Game Mode Refinements
- **Sequence Mode:**
    - Animate the `targetSequence` items in the Study Phase using `AnimatedVisibility` with a "sliding-in" effect from the right.
    - Use a "Wobble" animation on the `fatigueBar` when the player selects a wrong item.
- **Clarity Mode:**
    - Improve the `animateColorAsState` for word buttons by adding a "glow" border when a word is selected.
    - Add a "strikethrough" animation or fade-out for words that have been successfully found.

## 4. UI Modernization
- **Modern Chips:** Update `SuggestionChip` styles to use rounded corners (24.dp) and a subtle gradient fill instead of solid colors.
- **Fatigue Bar:** Add a "pulse" animation to the Fatigue bar when it reaches 80% capacity to warn the player.
