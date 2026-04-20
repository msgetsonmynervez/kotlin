# QA Recommendation Report

## Context

This report reviews the current Android app state with emphasis on navigation, global shell behavior, screen chrome consistency, and recent UI gating changes.

The main concern is not visual polish. The main concern is architectural instability hidden behind superficially organized Compose code.

## Executive Summary

The app is in the middle of a shell and navigation refactor. The codebase currently shows the classic failure mode of AI-assisted implementation:

- abstractions were introduced quickly
- old architecture was only partially removed
- route and shell assumptions were changed in multiple places at once
- UI behavior now depends on brittle string matching and feature-route shortcuts

The result is a codebase that looks cleaner than it is. It is not yet trustworthy.

## Primary Findings

### 1. Bottom-nav route visibility is implemented incorrectly for parameterized routes

File:

- `app/src/main/java/com/sterlingsworld/core/navigation/AppScaffold.kt`

Problem:

- `shouldShowBottomNav(route)` uses direct route-string equality
- `Screen.KidzCinema.route` is a pattern route: `kidz_cinema/{videoId}`
- the runtime route is a concrete filled route, not the pattern
- therefore the visibility rule for `KidzCinema` will never match in practice

Impact:

- bottom-nav behavior is inconsistent
- the route policy claims one thing and runtime behavior does another
- this makes the new global shell untrustworthy

Recommendation:

- stop relying on raw route-string equality for parameterized destinations
- use destination hierarchy, route grouping, or a normalized route classifier

### 2. The app shell is anchored to `Arcade` as an implicit root

Files:

- `app/src/main/java/com/sterlingsworld/core/navigation/AppScaffold.kt`
- `app/src/main/java/com/sterlingsworld/core/navigation/NavGraph.kt`

Problem:

- tab navigation uses `popUpTo(Screen.Arcade.route)`
- map navigation also uses `popUpTo(Screen.Arcade.route)`
- completion recovery returns to `Screen.Arcade.route`

Impact:

- `Arcade` is now being treated like a structural root instead of a feature destination
- this creates fragile back-stack behavior
- it will become difficult to reason about history restoration and tab switching

Recommendation:

- introduce a real app-level navigation root strategy
- do not hard-code a feature tab as the global pop target

### 3. Old route architecture still exists in the route model after the shell migration

Files:

- `app/src/main/java/com/sterlingsworld/core/navigation/NavRoutes.kt`
- `app/src/main/java/com/sterlingsworld/MainActivity.kt`
- `app/src/main/java/com/sterlingsworld/core/navigation/NavGraph.kt`

Problem:

- `Screen.Park` still exists in the route registry
- startup behavior no longer uses `Screen.Park`
- the app now jumps directly to `Screen.Arcade` on same-day relaunch

Impact:

- route declarations no longer match real app architecture
- dead routes and stale shell concepts remain in the codebase
- future work will be built on contradictory assumptions

Recommendation:

- either restore the intended `Park` shell architecture or fully remove it
- do not keep dead route concepts in the core navigation contract

### 4. `KidzCinemaScreen` contains a dead top-right control

File:

- `app/src/main/java/com/sterlingsworld/feature/kidzcinema/KidzCinemaScreen.kt`

Problem:

- the top-left `DashedCornerButton` is wired to back
- the top-right `DashedCornerButton` is rendered with no action

Impact:

- visible dead control in user-facing UI
- signals sloppy implementation and insufficient behavioral review

Recommendation:

- remove it or wire it intentionally
- do not ship decorative controls that imply interactivity

### 5. Settings now has duplicated navigation chrome

Files:

- `app/src/main/java/com/sterlingsworld/core/navigation/AppScaffold.kt`
- `app/src/main/java/com/sterlingsworld/feature/settings/SettingsScreen.kt`

Problem:

- the new app shell can render top-level actions
- `SettingsScreen` still renders its own top-left and top-right controls

Impact:

- overlapping navigation responsibilities
- inconsistent screen chrome
- likely layout and affordance duplication on real devices

Recommendation:

- audit screens after the shell migration
- decide which controls belong to the global shell versus the screen
- remove duplicated controls systematically

### 6. `Lucky Paws` was marked unavailable only at the presentation layer

Files:

- `app/src/main/java/com/sterlingsworld/feature/arcade/GrandArcadeIndoorScreen.kt`
- `app/src/main/java/com/sterlingsworld/feature/luckypaws/LuckyPawsScreen.kt`
- `app/src/main/java/com/sterlingsworld/core/navigation/NavGraph.kt`

Problem:

- the arcade card is disabled visually
- the launch page says unavailable
- but the route still exists and the game launch wiring still exists

Impact:

- feature disablement is weak
- any stale route path can still reach the screen
- the app advertises unavailability while still structurally exposing the feature

Recommendation:

- add central route-level gating for unavailable features
- do not rely on screen copy and disabled list cards alone

### 7. Startup behavior was changed during the shell refactor without strong architectural cleanup

File:

- `app/src/main/java/com/sterlingsworld/MainActivity.kt`

Problem:

- startup now targets `Screen.Arcade` on same-day reopen
- this is a meaningful app-flow change, not just a refactor detail

Impact:

- product behavior may have changed implicitly
- old flow assumptions in docs and route contracts may now be wrong

Recommendation:

- explicitly decide the intended post-welcome landing route
- align code, docs, and route contracts around that decision

## Root Cause Pattern

The code shows a repeated pattern:

- a real problem existed
- a global abstraction was introduced
- the abstraction was not backed by a full contract cleanup
- local screens retained old assumptions
- parameterized routes and runtime behavior were not validated carefully

This is exactly the type of defect pattern produced by AI-driven implementation without hard architectural discipline.

## Priority Recommendations

### Priority 1: Stabilize the navigation contract

- remove or restore `Screen.Park` intentionally
- define one real app root
- stop using `Arcade` as a fake structural root
- fix route classification for parameterized screens

### Priority 2: Audit shell ownership

- decide what belongs in `AppScaffold`
- remove duplicate top-level chrome from screens that are now shell-managed
- verify all screens with fixed artwork and top controls

### Priority 3: Add route-level feature gating

- unavailable features should be blocked centrally
- UI labels alone are not a feature kill switch

### Priority 4: Perform route-by-route runtime QA

Manually validate:

- all top-level tabs
- storybook and kidz cinema flow
- settings
- completion
- launch pages
- game shell
- video player

For each route, verify:

- top bar behavior
- bottom-nav visibility
- selected tab state
- back-stack behavior
- no dead controls

## Suggested Remediation Sprint

### Phase 1

- fix route classification for parameterized destinations
- remove hard-coded `popUpTo(Screen.Arcade.route)` shell assumptions

### Phase 2

- resolve whether `Screen.Park` is real or dead
- clean up route declarations accordingly

### Phase 3

- audit and remove duplicated screen chrome under the new shell

### Phase 4

- add central unavailable-feature gating

### Phase 5

- run a focused runtime QA pass on all navigation-critical routes

## Bottom Line

The app is not suffering from one bug. It is suffering from partial architectural edits that were never carried through to completion.

The most dangerous thing in the current state is that the code looks more coherent than it is. That invites further feature work on top of unstable assumptions.

The correct next move is not more UI work. It is navigation hardening, contract cleanup, and runtime verification.
