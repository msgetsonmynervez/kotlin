# Implementation Brief: Grand Arcade "Neon Premium" Pass

**Goal:** Transform the Grand Arcade Indoor Screen into a high-fidelity, high-juice experience matching the "Cyber-Neon" aesthetic of **IMG_4004**.

## 1. Scene Layout
- **Background:** Use `Image(painterResource(R.drawable.bg_grand_arcade_indoor))` with `ContentScale.Crop`.
- **Visibility Spacing:** Use a `LazyColumn` for the game list. Add a `Spacer(modifier = Modifier.height(220.dp))` as the first item to ensure the "Grand Arcade" neon sign in the background art is fully visible on entry.
- **Scrim:** Apply a `Brush.verticalGradient` overlay (Transparent -> Black 80%) to the bottom half of the screen to ensure card readability while scrolling.

## 2. The Neon Game Card (Component)
Replace the current simple list items with a `NeonArcadeCard` composable:
- **Background:** `Color.Black.copy(alpha = 0.75f)` with a `16.dp` rounded corner.
- **Neon Border:** A `2.dp` border using a `Brush.linearGradient` (Cyan `#00f2ff` to Pink `#ff007f`).
- **Internal Structure (Row):**
    - **Thumbnails:** `80.dp` square image on the left, rounded corners (`12.dp`), showing the game's key art.
    - **Content (Column):** Title in Bold White (`18.sp`) and a 2-line description in Muted Grey (`13.sp`).
    - **Play Button:** A custom capsule button with a heavy glow effect (`shadow` or `drawBehind`) that matches the neon border color.

## 3. Interaction & "Juice"
- **Entrance Animation:** Use `Modifier.graphicsLayer` and `LaunchedEffect` to animate cards sliding in from the bottom and fading in with a 100ms stagger between items.
- **Hover/Touch State:** Slightly increase the card's `scale` (1.02x) and border `alpha` when the user touches it.
- **Haptics:** Trigger `LocalContext.current.vibrate(20)` on button taps.

## 4. "Coming Soon" Styling
For games where `isLive == false`:
- **Grayscale:** Apply a grayscale `ColorFilter` to the card thumbnail.
- **Muted Border:** Change the neon border to a static, dim grey.
- **Overlay:** Add a centered "COMING SOON" label in a bold, condensed font across the card.

## 5. Data Wiring
- Update the `ArcadeEntry` class to include:
    - `description: String`
    - `thumbnailRes: Int`
- Populate the `arcadeGames` list with thematic data for all active and blocked games.
