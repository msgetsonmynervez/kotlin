# Bottom Navigation Sprint Plan

## Objective

Make the bottom navigation bar available consistently across the app where it makes product sense, using one app-level implementation instead of screen-by-screen duplication.

This is not a styling task. It is a navigation architecture refactor.

## Current State

The current bottom navigation only exists inside `ParkScaffold`.

- `ParkScaffold` is mounted only at `Screen.Park`
- its internal tab graph covers only:
  - `Screen.Arcade`
  - `Screen.Cinema`
  - `Screen.Studio`
  - `Screen.Kidz`
  - `Screen.Map`
- most real app routes live outside that scaffold and therefore do not get the bottom nav

Important files:

- `app/src/main/java/com/sterlingsworld/core/navigation/NavGraph.kt`
- `app/src/main/java/com/sterlingsworld/core/navigation/ParkScaffold.kt`
- `app/src/main/java/com/sterlingsworld/core/navigation/NavRoutes.kt`
- `app/src/main/java/com/sterlingsworld/MainActivity.kt`

## What Must Not Happen

Do not copy-paste a bottom bar into individual screens.

Do not keep `ParkScaffold` as the only owner of tab navigation if the goal is app-wide bottom navigation.

Do not force the bottom bar onto full-screen runtime surfaces without an explicit decision. In particular, treat these as likely hidden-by-design:

- `GameShellScreen`
- `VideoPlayerScreen`
- native activity handoff / launcher routes

## Recommended Product Behavior

### Show bottom nav on:

- park section pages
  - arcade
  - cinema
  - studio
  - kidz
  - map
- settings
- attraction landing / launch pages
  - `GrandArcadeIndoor`
  - `LuckyPaws`
  - `Gauntlet`
  - `SymptomStriker`
  - `Creamery`
  - `RelaxationRetreat`
  - `Aol`
  - `Doodle`
  - `Linebreaker`
  - `LumiStarQuest`
  - `Nostalgia`
- kidz chooser / menu flows
  - `KidzArcadeMenu`
  - `StorybookLand`
  - `KidzCinema` only if product wants it to behave like a normal page before playback

### Hide bottom nav on:

- `Welcome`
- `GamePlayer`
- in-app game runtime content
- native game launcher handoff
- `VideoPlayer`
- `Completion` unless product explicitly wants tabs there
- `TechnicalDifficulties` if intended as a blocking fallback surface

## Sprint Scope

### Phase 1: Create an app-level scaffold

Goal: move bottom-nav ownership above the root nav host.

Tasks:

- create a new app-level scaffold composable, for example `AppScaffold`
- mount it above the root `NavHost` instead of inside `Screen.Park`
- give it access to the root `NavController`
- move the bottom tab definition out of `ParkScaffold` into a shared top-level navigation model

Expected result:

- there is exactly one bottom navigation implementation in the app
- it can react to any current route in the root graph

### Phase 2: Add route visibility policy

Goal: make bottom-nav visibility explicit and centralized.

Tasks:

- add a route policy helper, for example:
  - `fun shouldShowBottomNav(route: String?): Boolean`
- define visibility using route names from `Screen`
- keep the policy in one place, near navigation code
- avoid scattering route checks across screens

Expected result:

- visibility decisions are deterministic
- adding a new route later requires changing one policy table, not multiple screens

### Phase 3: Rework tab navigation to use the root controller

Goal: make the bottom bar navigate directly across app sections.

Tasks:

- remove the internal nested tab ownership from `ParkScaffold`
- make the bottom bar use the root `NavController`
- preserve `launchSingleTop`, state restoration, and sensible `popUpTo` behavior
- keep the selected tab derived from the current root route

Expected result:

- moving between `Arcade`, `Cinema`, `Studio`, `Kidz`, and `Map` works from anywhere bottom nav is shown
- no duplicate navigation state exists between root and nested tab controllers

### Phase 4: Convert `ParkScaffold`

Goal: simplify `ParkScaffold` so it stops acting as a hidden nested app shell.

Tasks:

- remove the bottom bar from `ParkScaffold`
- either:
  - delete `ParkScaffold` entirely and move any needed top app bar behavior into the new app shell, or
  - reduce `ParkScaffold` to a plain content wrapper if still needed
- make sure `Screen.Park` no longer serves as a special navigation island

Expected result:

- park routes become normal routes in the global app shell
- app navigation becomes understandable from one place

### Phase 5: Audit screen chrome conflicts

Goal: avoid UI collisions once the bottom bar is global.

Tasks:

- review screens with fixed bottom-aligned controls or full-screen art
- verify no important content is hidden behind the bar
- adjust padding / safe area handling where needed
- review per-screen back buttons and utility buttons:
  - `BathroomMapButton`
  - `DashedCornerButton`

Expected result:

- the bottom bar does not overlap critical CTA areas
- screens still feel intentional, not layered randomly

### Phase 6: Validate full route groups

Goal: confirm the new shell behaves consistently.

Test matrix:

- top-level section routes
  - `Arcade`
  - `Cinema`
  - `Studio`
  - `Kidz`
  - `Map`
- settings
- arcade launch pages
- kidz chooser and storybook flows
- game launch screens
- completion
- game shell
- video playback

For each route, verify:

- bottom nav shown or hidden intentionally
- selected tab state is correct
- back behavior still makes sense
- no clipped content at the bottom

## Implementation Notes

### Best structural approach

Use a root scaffold pattern:

- `MainActivity`
  - theme
  - root `NavController`
  - `AppScaffold(navController)`
    - top app bar if needed
    - bottom nav if `shouldShowBottomNav(currentRoute)`
    - root `NavHost`

This is the cleanest direction because it matches the product goal: one app, one shell.

### Avoid this anti-pattern

Do not keep:

- root nav host
- nested `ParkScaffold`
- nested tab nav host

and then try to “mirror” the bottom bar globally. That will keep producing inconsistent state and route bugs.

## Risks

### High risk

- nested nav state currently hidden inside `ParkScaffold`
- breaking selected-tab restoration when moving to root navigation

### Medium risk

- bottom padding regressions on image-heavy launch pages
- collision with fixed CTA placement on attraction screens

### Low risk

- settings and normal content screens should be easy once the root shell exists

## Definition of Done

- one app-level bottom navigation implementation exists
- bottom-nav visibility is centrally controlled by route policy
- all intended pages show the same bottom nav consistently
- excluded routes hide it intentionally
- no nested tab-shell duplication remains
- route changes preserve sensible back-stack behavior
- major content screens remain visually correct with the new bottom inset

## Suggested Execution Order

1. Create `AppScaffold`
2. Move tab model and selected-tab logic to root navigation
3. Add `shouldShowBottomNav`
4. Remove bottom bar from `ParkScaffold`
5. Reconnect top-level routes directly to root tabs
6. Fix per-screen padding / overlap issues
7. Run route-by-route validation

## Deliverable For This Sprint

At the end of the sprint, Claude should report:

- which files were changed
- whether `ParkScaffold` still exists and what it now does
- the final route visibility policy
- which routes intentionally hide bottom nav
- any screens that needed layout adjustment
- what was manually verified
