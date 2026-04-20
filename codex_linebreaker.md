# Implementation Brief: Linebreaker "Neon Arcade" Pass

**Goal:** Transform the standalone "Line Builder" HTML5 game into a visually stunning, integrated part of the Sterling's World arcade suite.

## 1. Visual Aesthetics (CSS)
- **Background Integration:** Set the `body` background to `transparent`. The Kotlin `WebView` will handle rendering `bg_linebreaker.jpg` behind it.
- **Midnight Palette:** 
    - Change the `board-wrap` background to `rgba(8, 1, 15, 0.85)` (deep midnight purple).
    - Update `COLORS` in JS to use more vibrant, neon-style hex codes:
        - Cyan: `#00f2ff`, Yellow: `#ffd700`, Purple: `#bc13fe`, Green: `#39ff14`, Pink: `#ff007f`, Orange: `#ff6700`, Blue: `#0047ab`.
- **Neon Glows:** Apply `box-shadow` to the falling pieces (`current`) and the "Ghost" piece to create a neon-light effect. Use `ctx.shadowBlur = 15;` in the Canvas draw logic.

## 2. Gameplay Feedback (Juice)
- **Line Clear Effect:** When a line is cleared, trigger a 200ms white "Flash" animation on that specific row before it disappears.
- **Shake Effect:** Add a `screen-shake` class to the `canvas` element that triggers briefly whenever a piece "Locks" or a "Hard Drop" occurs.
- **Particles:** On line clear, spawn 10-20 tiny square particles that float outward from the cleared line and fade out.

## 3. UI & Typography
- **Typography:** Update the CSS to use `'Bungee', 'Inter', sans-serif` (or the app's primary bold font).
- **Overlay Redesign:** 
    - The `overlay` should use a semi-transparent dark blur (`backdrop-filter: blur(8.dp)`).
    - The `play-btn` should have a neon border and pulse gently when the game is not active.

## 4. Android Integration (Bridge)
- **Haptics:** Add `window.Android?.vibrate(50)` calls in the JavaScript `lock()` and `clearLines()` functions to provide physical feedback.
- **Sound:** Call `window.Android?.playArcadeSound('clear')` when lines are cleared.

## 5. Performance
- Ensure the `requestAnimationFrame` loop is efficient and doesn't leak memory on repeated "Retry" clicks.
