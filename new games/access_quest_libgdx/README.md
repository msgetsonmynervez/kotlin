# Access Quest – libGDX/Kotlin Prototype

This repository contains a minimal skeleton for **Access Quest**, implemented in **Kotlin** using the **libGDX** game framework.  The goal of this prototype is to show how the core systems (player movement, fatigue/heat meters, checkpoints, items, hazards and progression) can be set up in a cross‑platform game codebase targeting Android.

libGDX is a cross‑platform game framework that allows you to deploy the same codebase to Windows, Linux, macOS, Android, iOS and HTML5【385060522982807†L16-L48】.  It provides an environment for rapid prototyping without imposing a specific architecture【385060522982807†L23-L26】.  The project uses Kotlin because it is a modern statically typed language with C#‑like features such as null safety, extension functions, lambdas and string interpolation【854626278484611†L15-L47】.  Kotlin integrates seamlessly with Java libraries and is fully interoperable with libGDX【854626278484611†L42-L56】.

## Project structure

The project is organised into two Gradle modules:

| Module  | Description |
|--------|-------------|
| **core** | Contains all cross‑platform game logic, written in Kotlin.  This includes the `AccessQuestGame` class (entry point), game screens, player control, fatigue/heat systems, item definitions and world map structures. |
| **android** | Android launcher module that packages the core module into an Android application.  It contains a simple `AndroidLauncher` class that extends `AndroidApplication` and sets up the game. |

To generate a working project with libGDX and Kotlin support, you can use the [gdx‑liftoff](https://github.com/tommyettinger/gdx-liftoff) project generator and then replace its `core` and `android` directories with the ones provided here.  Alternatively, copy the `core` module into your existing libGDX project and register `AccessQuestGame` as the entry point.

## Getting started

1. Install [Android Studio](https://developer.android.com/studio) and [Gradle](https://gradle.org/) if you haven’t already.  Ensure that Kotlin plugin is enabled.
2. Use gdx‑liftoff or any libGDX starter to create a Kotlin‑enabled project with `core` and `android` modules.  Select `Kotlin` language and include the `android` backend.  gdx‑liftoff will configure the Gradle files for you and add Kotlin dependencies.
3. Replace the contents of the generated `core` and `android` directories with the files in this repository.  Ensure that the package names match (`com.accessquest`).
4. Open the project in Android Studio and run the **android** module on an emulator or device.

## Features in this skeleton

* **Top‑down movement:** The `Player` class reads directional input from the `Input` class every frame and translates it into velocity, moving a sprite on a 2D plane.  This follows libGDX’s recommended approach for handling input and actor movement【699030086918637†L141-L167】.
* **Fatigue and heat systems:** `FatigueSystem` and `HeatSystem` accumulate values as the player moves and over time.  When a threshold is reached, the player is returned to the last checkpoint, encouraging smart route choice rather than punishing with a full fail state.
* **Checkpoints:** The `CheckpointManager` stores the last safe location and recovers some fatigue and heat when activated.
* **Item system:** `Item` is a base class for accessibility tools such as the cane, supportive shoes, cooling vest, mobility scooter, reacher, access assist and recovery support.  Items modify player speed, fatigue or heat accumulation via extension properties when equipped.  Items are defined as Kotlin objects in the `items` package.
* **Hazards:** `Hazard` types (e.g., heat zones, rough terrain, slippery surfaces) modify fatigue/heat multipliers when the player enters their bounding rectangle.  This encourages players to find safer or smarter routes.
* **World map and progression:** `WorldMap` holds a list of `MapNode`s representing levels.  Completing a level unlocks new nodes and rewards items, enabling branching progression.

## Next steps

This skeleton focuses on the core logic; there is no rendering or UI yet.  To build a complete vertical slice, you’ll need to:

1. **Add assets:** Place sprites, tilemaps and UI skins in the `android/assets` folder.  Use libGDX’s `AssetManager` to load textures and fonts.
2. **Render entities:** Use `SpriteBatch` or `Scene2D` in the `GameScreen` to draw the player, checkpoints, hazards and destination markers.  Manage camera zoom and panning to ensure mobile readability.
3. **Implement UI:** Create fatigue and heat bars using `Scene2D` UI widgets or the `ktx` `scene2d` DSL.  Add buttons or virtual joysticks for touch input.
4. **Load and save:** Use libGDX’s `Preferences` to store currency, unlocked items and level completion flags between sessions.
5. **Playtesting:** Adjust fatigue/heat rates, item modifiers and level layouts until the game feels challenging yet respectful of the MS narrative.

libGDX’s active community and wiki provide many tutorials and examples if you need further guidance【385060522982807†L45-L76】.
