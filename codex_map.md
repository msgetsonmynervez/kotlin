# Implementation Brief: Map Screen "Integrated Glass" Pass

**Goal:** Remove the generic floating pill buttons and integrate the navigation directly into the new Map artwork using transparent glass hotspots.

## 1. Map Layout
- **Image Integration:** The `bg_theme_park_map.jpg` is a vertical stack of four cards. 
- **The Hotspots:** Instead of `Alignment.CenterStart`, etc., we will use a `Column` or a `Box` with percentage-based offsets to place transparent `Box` components exactly over the four illustrated cards.
- **Remove Old UI:** Delete the hardcoded "pill" buttons (`Box` with `Color(0xEE1A1A1A)`) and the "Music Land", "Movie Land" text, as the text is already in the artwork.

## 2. Glassmorphic Interaction
- **Card Selection:** Create a `MapZoneHotspot` composable that is a transparent `Box`.
- **Feedback:** On touch (`InteractionSource.collectIsPressedAsState`), show a `Box` overlay with:
    - `background(Color.White.copy(alpha = 0.1f))`
    - `border(1.dp, Color.White.copy(alpha = 0.3f))`
    - `Modifier.blur(4.dp)`
- **Alignment:** 
    - Zone 1 (Music): Top 25%
    - Zone 2 (Movie): 25% - 50%
    - Zone 3 (Games): 50% - 75%
    - Zone 4 (Kidz): Bottom 25%

## 3. Navigation
- Ensure the `onNavigateToZone` callback is triggered when the user taps anywhere within the card boundaries, with the strongest visual feedback centering on the illustrated "Explore" buttons.

## 4. Polish
- **Entrance:** Use a "Slide in from Top" animation for the whole map image.
- **Sound:** Trigger a "Map Paper" rustling sound effect when the screen opens.
