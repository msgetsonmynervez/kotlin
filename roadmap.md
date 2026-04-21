# MeetSterling Android Roadmap

Current canonical app:

- `C:\Users\Gutie\projects\kotlin-clean`

Reference-only legacy repo:

- `C:\Users\Gutie\projects\Sterlingsworld`

This document now serves as the current-state roadmap and release snapshot for the final working branch, not an early migration plan.

## What Is Done

### Native app foundation

- Native Android project is the canonical app
- Gradle-based Android build is working from the repo root
- Typed navigation, shell screens, theme, and app wiring are in place
- The app runs through the current Kotlin navigation shell rather than the old RN path
- The debug app was revalidated on emulator after a stale build artifact caused a temporary startup crash

### Media and content

- Video assets are staged in the native app
- HTML game assets live under `app/src/main/assets/games/` and launch through `WebViewGame`
- Studio audio corpus is staged in the `:studio-audio` PAD module
- Studio catalog exposes:
  - available now: `Dark Side of the Spoon`, `Stand Up`, `Neural Garden`
  - download later: `Groove`, `Sterling Main Library`

### Runtime and shell work

- `GameShellScreen` and `GameShellViewModel` handle pause, resume, restart, exit, and completion
- Progress events are recorded through the native progress path
- Completion replay and routing exist in the shared navigation flow
- `VideoPlayerScreen` uses Media3/ExoPlayer for Cinema and Kidz playback
- Attraction landing screens route into either native runtimes or packaged HTML/WebView assets
- Image-heavy landing screens use artwork tap targets instead of older visible pill buttons

### Current game surface

Ship-ready in catalog:

- `Ghost`
- `Cognitive Creamery`
- `Symptom Striker`
- `Spoon Gauntlet`
- `Relaxation Retreat`

Preview-live in app:

- `Busy Streets`
- `Spoons and Stairs`

Integrated but not ship-ready:

- `AOL`
- `Lucky Paws`
- `Access Quest`
- `Access Racer`
- `Snail's Journey`

Kidz surfaces present in app:

- `Kidz Doodle Land`
- `Kidz Linebreaker`
- `Lumi's Star Quest`
- `Nostalgia`
- Storybook Land / Kidz Cinema through the shared player

## Verification Completed On This Branch

- Android Studio / Gradle sync path is intact
- `:app:assembleDebug`
- `:app:installDebug`
- emulator relaunch after clean rebuild and reinstall
- targeted native game/unit coverage remains in repo for catalog and ViewModel flows

## Current Known Caveats

- Studio still needs honest PAD/runtime validation on the intended install path
- Bottom-nav and shell route-policy cleanup is still open
- `Busy Streets` and `Spoons and Stairs` are intentionally treated as preview launches, not ship-ready titles
- `AOL` remains implemented but not promoted as live in the arcade surface
- `Lucky Paws` remains unavailable in the current surfaced experience

## Recommended Follow-Up Order

1. Run a final manual smoke pass across surfaced routes.
2. Validate Studio on the intended delivery/install path.
3. Clean up bottom-nav and route-policy drift.
4. Decide whether `Busy Streets` and `Spoons and Stairs` should stay preview-only or be promoted to ship-ready after polish.
