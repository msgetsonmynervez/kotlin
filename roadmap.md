# MeetSterling Android Roadmap

Current canonical app:

- `C:\Users\Gutie\projects\kotlin-clean`

Reference-only legacy repo:

- `C:\Users\Gutie\projects\Sterlingsworld`

## Where We Started

The original shipping path was a React Native project with native Android and iOS shells around it. That repo had become the wrong base for release work:

- native build issues were being caused by RN dependencies and tooling drift
- media and game assets needed to live in the native Android app
- the long-term goal was a native Android app, not continued RN maintenance

The project was therefore re-centered on the native Android repo and the RN repo was treated as source material only.

## What Is Done

### Native app foundation

- Native Android project is the canonical app
- Gradle-based Android build is working again
- Typed navigation, shell screens, theme, and app wiring are in place
- The app now runs through the current Kotlin navigation shell rather than the old RN path
- App builds successfully with `:app:assembleDebug`

### Asset and media migration

- Video assets are staged in the native app
- Game asset corpus has been copied into the native Android project
- HTML game assets now live under `app/src/main/assets/games/` and launch through `WebViewGame`
- Studio audio corpus is staged in the `:studio-audio` PAD module
- Studio catalog now exposes:
  - available now: `Dark Side of the Spoon`, `Stand Up`, `Neural Garden`
  - download later: `Groove`, `Sterling Main Library`

### Runtime and shell work

- `GameShellScreen` and `GameShellViewModel` handle pause, resume, restart, exit, and completion
- progress events are recorded through the native progress path
- completion replay and routing exist in the shared navigation flow
- `VideoPlayerScreen` uses Media3/ExoPlayer for Cinema and Kidz asset playback
- attraction landing screens route into either native runtimes or packaged HTML/WebView game assets
- image-heavy landing screens now use artwork tap targets instead of visible pill buttons

## Verification already completed

The following are currently green:

- Android Studio sync
- targeted native unit tests
  - `GameCatalogTest`
  - `GameShellViewModelTest`
  - `LuckyPawsViewModelTest`
  - `GhostViewModelTest`
  - `CognitiveCreameryViewModelTest`
  - `SymptomStrikerViewModelTest`
- `:app:assembleDebug`

## Current App Surface

### Currently live in app

These routes are currently presented as active in the app code or game catalog:

- `Symptom Striker`
- `Cognitive Creamery`
- `Spoon Gauntlet`
- `Relaxation Retreat`
- `AOL`
- `Ghost`

### Currently unavailable or deferred

- `Lucky Paws` is intentionally marked unavailable / coming soon
- `Access Quest`, `Access Racer`, and `Snail's Journey` exist in code/catalog but are not currently live in the surfaced app flow

### Kidz surfaces present in app

- `Kidz Doodle Land`
- `Kidz Linebreaker`
- `Lumi's Star Quest`
- `Nostalgia`
- Storybook Land
- Kidz Cinema via the shared video player

### Studio

- Studio UI, catalog, and transport path exist
- the catalog is no longer a single generic library
- only three albums are intended as available now:
  - `Dark Side of the Spoon`
  - `Stand Up`
  - `Neural Garden`
- `Groove` and `Sterling Main Library` are visible as download-later albums
- Studio still needs honest runtime validation on device / Play-installed delivery before it can be treated as fully verified

## What Is Next

## 1. Manual QA pass

This is still the highest-priority next step.

Priority order:

1. `Symptom Striker` full win path
2. `Symptom Striker` loss path
3. `Ghost` full run
4. completion replay and back-to-map flow
5. pause and restart behavior in at least two games
6. `Cognitive Creamery`, `Spoon Gauntlet`, `Relaxation Retreat`, and `AOL` route sanity
7. confirm `Lucky Paws` stays unavailable and cannot be entered through normal UI flow

Goal:

- confirm the verified code paths also behave correctly on emulator or device
- confirm attraction-screen routing and WebView-backed titles load cleanly

## 2. Fix anything found in manual QA

Only after the manual pass.

If failures appear, fix them before starting another implementation sprint.
The rule remains:

- do not stack new feature work on top of broken current flows

## 3. Validate Studio runtime honestly

This remains the main media unknown.

Need to prove:

- the three intended live albums resolve correctly at runtime
- playback works on the actual intended install path
- the visible download-later albums remain non-playable

## 4. Navigation and shell hardening

The app shell has been changing quickly and needs cleanup work that is already documented in `bottom.md`.

The main focus:

- make bottom navigation behavior intentional and consistent
- remove stale route assumptions
- verify shell chrome does not conflict with full-screen pages

## 5. Next feature promotion sprint

If QA is clean, the next logical promotion candidate is still:

- `AOL`

Fallback:

- battle-family hardening first if the shared battle foundation still needs cleanup before another promotion

## 6. Release hardening

After current flows are verified:

- broader device smoke testing
- release packaging review
- signing and distribution prep
- final scope cleanup so docs, catalog state, and surfaced UI all say the same thing

## Rules For Tomorrow

- Start with manual QA, not a speculative feature
- Keep this repo as the main build and only shipping target
- Do not re-open RN work unless a missing asset or source reference requires it
- Keep docs aligned with actual code state, especially for Studio and Lucky Paws

## Current Restart Point

If work resumes tomorrow, the first action should be:

- run the manual checklist and report failures only

Priority order:

- `Symptom Striker` win path
- `Symptom Striker` loss path
- `Ghost` full run
- completion replay and back-to-map flow
- Studio runtime sanity

If no failures appear:

- begin the next hardening or promotion sprint centered on `AOL`
