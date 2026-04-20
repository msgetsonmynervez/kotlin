# Implementation Brief: Nostalgia "Retro-Modern" Polish Pass

**Goal:** Upgrade the classic Breakout game into a high-juice, atmospheric experience that feels like a premium retro arcade cabinet in Sterling's World.

## 1. Visual Aesthetics & Environment
- **Transparency:** Set the `body` background to `transparent` so the Kotlin-rendered `bg_nostalgia.jpg` is visible.
- **CRT Styling:** 
    - Keep the `body::after` scanline overlay but reduce its opacity to `0.05` for a subtler effect.
    - Add a slight `box-shadow: 0 0 25px rgba(51, 255, 51, 0.2)` to the `canvas-wrap` to make it look like a glowing monitor.
- **Neon Colors:** Refine `COLORS` and `HIT_COLORS` to use higher-intensity neon values:
    - Neon Green: `#39FF14`, Cyan: `#00F2FF`, Yellow: `#FFE700`, Red: `#FF3131`.

## 2. Dynamic Juice (The "Feel")
- **Impact Shake:** Create a `shakeBoard()` function that applies a brief `transform: translate(randomX, randomY)` to the canvas when the ball hits the paddle or a brick.
- **Particle System:** 
    - When a brick is destroyed (`b.alive = false`), spawn 8-10 small rectangular particles (`2x2` px) of the same color as the brick.
    - Animate them with random velocities and gravity, fading them out over 500ms.
- **Dynamic Ball Trail:** Improve the trail logic to draw multiple fading circles (`alpha` 0.1 to 0) along the ball's recent trajectory for a "comet" look.

## 3. UI & HUD
- **HUD Redesign:** Move the Score and Lives into stylized "LED display" boxes with a slight inner glow.
- **Typography:** Use a cleaner pixel-art font like `'Press Start 2P'` or a bold sans-serif with wide spacing for the "HUD" labels.

## 4. Bridge & Audio
- **Haptics:** Call `window.Android?.vibrate(30)` on every brick hit and `window.Android?.vibrate(60)` when a life is lost.
- **Audio Triggers:** Trigger retro sound effects via the bridge:
    - `window.Android?.playSound('blip')` for paddle/wall hits.
    - `window.Android?.playSound('crash')` for brick destruction.
    - `window.Android?.playSound('levelup')` for level completion.

## 5. Gameplay Polish
- **Ball Logic:** Ensure the ball speed increases slightly more aggressively per level to keep the challenge high.
- **Paddle Smoothness:** Add a small amount of "lerp" (interpolation) to the paddle's horizontal movement for a smoother feel on high-refresh-rate screens.
