# MeetSterling Android Kotlin Rebuild - Phase Status

Canonical app repo:

- `C:\Users\Gutie\projects\kotlin-clean`

This file reflects the current final working branch snapshot.

## Phase 1: Foundation - COMPLETE
**Completed:** 2026-04-12

- Gradle KTS project and Android app module are in place
- Core domain models, catalogs, navigation shell, application wiring, and theme are established
- Native Android project is the canonical app path

---

## Phase 2: Shell - COMPLETE

- Welcome, map, settings, completion, cinema, studio, arcade, and kidz surfaces exist
- Kidz entry path lands on the storybooks-or-games chooser
- Attraction and zone surfaces exist for the shipped and preview titles currently represented in app code
- Artwork tap targets replaced the older visible pill CTA pattern on landing pages

Remaining shell follow-up:

- bottom-nav and route-policy cleanup
- route-by-route shell chrome consistency pass

---

## Phase 3: Media - COMPLETE WITH STUDIO VALIDATION STILL CONDITIONAL

- Cinema and Kidz video assets are staged and routed through `VideoPlayerScreen`
- `VideoPlayerScreen` uses Media3/ExoPlayer against bundled app assets
- Storybook Land and Kidz Cinema route selected Kidz videos into the shared player
- Studio audio corpus is staged in the `:studio-audio` PAD module
- Studio exposes real album groupings:
  - available now: `Dark Side of the Spoon`, `Stand Up`, `Neural Garden`
  - download later: `Groove`, `Sterling Main Library`

Still conditional:

- Studio runtime validation on the intended install path / PAD delivery path

---

## Phase 4: Game Platform Foundation - COMPLETE

- `GameShellScreen` + `GameShellViewModel` provide pause, restart, exit, and completion flow
- progress repository integration records session start, restart, and completion
- real completion routing exists for the native game flows currently wired through the shell
- native game families live under `feature/game/games/`
- HTML/WebView runtime is wired for packaged asset games under `app/src/main/assets/games/`
- attraction detail screens route into either native runtimes or packaged WebView games through the shared `game_player/{gameId}` entry point

Still open at platform level:

- broader accessibility and haptics pass
- bottom-nav / shell consistency cleanup
- broader regression sweep across surfaced routes

---

## Phase 5: Individual Games - COMPLETE WITH TIERED READINESS

### Ship-ready in catalog

| Game | State |
|---|---|
| Ghost | Ship-ready |
| Cognitive Creamery | Ship-ready |
| Symptom Striker | Ship-ready |
| Spoon Gauntlet | Ship-ready |
| Relaxation Retreat | Ship-ready |

### Preview-live in app

| Game | State |
|---|---|
| Busy Streets (`frogger`) | Launchable preview |
| Spoons and Stairs | Launchable preview |

### Integrated but not ship-ready

| Game | State |
|---|---|
| AOL | Implemented route, not ship-ready, not promoted in arcade UI |
| Lucky Paws | Present but unavailable / not ship-ready |
| Access Quest | Present in catalog/runtime path, not ship-ready |
| Access Racer | Present in catalog/runtime path, not ship-ready |
| Snail's Journey | Present in catalog/runtime path, not ship-ready |

### Kidz titles present in app

| Game | State |
|---|---|
| Kidz Doodle Land | Present |
| Kidz Linebreaker | Present |
| Lumi's Star Quest | Present |
| Nostalgia | Present |
| Storybook Land / Kidz Cinema | Routed through shared player flow |

Current game-specific note:

- `Busy Streets` now uses integrated Frogger preview art for traffic and raft obstacles

---

## Phase 6: Hardening - COMPLETE (Architecture + Runtime Recovery)
**Completed:** 2026-04-21

- Domain-layer repository interfaces (`AppPreferencesRepository`, `GameProgressRepository`) are in place
- AppContainer and ViewModels depend on domain abstractions instead of concrete data implementations
- Progress persistence uses Room `@Transaction` and atomic updates
- WebView navigation is restricted to local assets
- Speech/audio support changes are in place for the native game stack
- Shadow `new games` directory has been removed from the shipping path
- Preview arcade titles for Frogger and Spoons are integrated into the main app path
- Current debug launch path was revalidated after a stale APK artifact caused a startup failure; a clean rebuild and reinstall restored normal startup

Still open:

- manual smoke pass for all surfaced routes
- Studio PAD/runtime validation on the intended install path
- final shell cleanup so bottom-nav behavior stays consistent
