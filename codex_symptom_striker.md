# Implementation Brief: Symptom Striker Polish Pass

**Goal:** Transform the static Symptom Striker battle UI into an interactive, high-juice "flagship" experience while preserving the core ViewModel logic.

## 1. Scene Composition
- **Background Integration:** Wrap the entire `BattleField` in a `Box`. Use `Image(painterResource(R.drawable.bg_symptom_striker))` as the bottom-most layer with `ContentScale.Crop`.
- **Transparency:** Change `Card` and `Surface` colors in the battle UI to use `Color.Black.copy(alpha = 0.6f)` or `Overlay` to allow the background art to show through.
- **Blur:** Apply `Modifier.blur(8.dp)` to the background image when Overlays (Intro, Win, Loss) are visible.

## 2. Combat Feedback (The "Juice")
- **Damage Shake:** Implement a `Modifier` that triggers a small random X/Y offset shake when `state.playerHp` or `state.enemyHp` decreases.
- **Impact Numbers:** Create a floating `Text` component that spawns at the center of the screen when damage/healing occurs.
    - *Style:* Red bold text for damage (e.g., `-15`), Green for healing (e.g., `+5`).
    - *Animation:* Animate `alpha` from 1 to 0 and `offsetY` upwards over 1000ms.
- **Turn Transition:** Add a subtle `AnimatedContent` transition when switching from `PLAYER_TURN` to the Enemy's resolution phase.

## 3. Sprite Styling
- **Neon Text Art:** Wrap the monospace `enemySprite` in a `Box` and apply a `Modifier.shadow` or a custom `drawBehind` glow effect that matches the enemy's state (Normal = Blue/Cyan glow, Enraged = Red/Orange pulse).

## 4. Audio Triggers
- **Combat Sounds:** Add calls to a `SoundManager` (or similar) within the `SymptomStrikerGame` Composable based on state changes:
    - `onMoveSelected`: Play "Click/Select" sound.
    - `playerHp` decrease: Play "Impact" sound.
    - `enemyHp` decrease: Play "Success/Hit" sound.
    - `BattlePhase.ENCOUNTER_WIN`: Play "Fanfare" sound.

## 5. UI Layout Refinement
- **The Spoon Row:** Replace the simple `Box(CircleShape)` with an SVG or Vector icon of a spoon (`ic_spoon`).
- **Log Visibility:** Limit the `BattleLog` to the last 3 messages only to save vertical space, adding a "See full log" expandable option if needed.
