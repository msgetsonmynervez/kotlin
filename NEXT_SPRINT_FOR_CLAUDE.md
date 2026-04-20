# Next Sprint For Claude

## Objective

The next sprint is a hardening sprint for the main build in this repo.

It is not a feature-explosion sprint.
It should make the current surfaced app materially more trustworthy and make the docs match the actual product state.

## Current checkpoint summary

What is solid:

- app structure and Gradle wrapper flow
- current Kotlin navigation shell
- welcome to map flow
- themed attraction screens and tappable artwork landing surfaces
- native game shell and completion flow
- Media3-backed video playback for Cinema and Kidz
- Studio catalog with real album groupings
- targeted unit tests for catalog and current native gameplay flows

What is still weak:

- no full manual QA pass against the current surfaced build
- Studio runtime still needs honest device validation
- bottom-nav and route-policy cleanup is still open
- Lucky Paws is intentionally unavailable, but older docs and assumptions were lagging behind until this cleanup
- app docs have been updated, but future changes must keep them aligned with code

## Sprint goals

Claude must complete the following in this sprint.

### 1. Re-verify build and test baseline on the main build

Required outcomes:

- prove this main build still builds from the project root
- rerun the targeted unit test suite that covers catalog and current live gameplay flows
- report exact commands used and whether they passed

Required reporting:

- exact command used
- whether it passed
- if it failed, exact blocker and file

### 2. Run a manual QA pass on the current surfaced routes

Required outcomes:

- verify the currently live game routes:
  - `Ghost`
  - `Cognitive Creamery`
  - `Symptom Striker`
  - `Spoon Gauntlet`
  - `Relaxation Retreat`
  - `AOL`
- verify Kidz entry routing into the chooser page
- verify Cinema and Kidz video routing through the shared player
- verify `Lucky Paws` remains unavailable through normal UI flow

Required reporting:

- what was exercised
- exact failure points
- whether failures are routing bugs, runtime bugs, asset-loading bugs, or state bugs

### 3. Validate Studio honestly

Required outcomes:

- prove whether the intended live albums resolve on the actual install path
- verify that only these albums are treated as currently available:
  - `Dark Side of the Spoon`
  - `Stand Up`
  - `Neural Garden`
- verify `Groove` and `Sterling Main Library` stay visible but non-playable

Required reporting:

- whether the blocker is tooling, install method, asset resolution, or playback
- whether Studio can be treated as actually ready or still needs to remain conditional

### 4. Clean up navigation drift

Required outcomes:

- review current bottom-nav and route-policy behavior
- use `bottom.md` as the execution guide if this sprint reaches navigation work
- do not patch this with per-screen duplication

Required reporting:

- what still behaves inconsistently
- whether the app shell needs another focused refactor sprint

## Explicit non-goals for this sprint

Do not:

- add more attraction pages just for visual coverage
- invent another new media structure
- mark Lucky Paws live again unless product explicitly changes that state
- claim Studio is solved without real runtime proof
- let docs drift away from actual app state again

## Deliverables required at end of sprint

Claude must provide:

1. Build status
   - exact wrapper commands
   - whether `assembleDebug` works
   - whether targeted tests run

2. QA status
   - pass/fail notes for current live routes
   - pass/fail notes for media routing
   - Lucky Paws availability check result

3. Studio status
   - runtime validation result
   - current live album validation result
   - remaining blocker if still not fully verified

4. Navigation status
   - what is still inconsistent
   - whether `bottom.md` should be the next focused sprint

## Completion standard

This sprint is successful only if the main build is more trustworthy at the end:

- verified by build and targeted tests
- manually exercised on the routes the app actually exposes
- honest about Studio readiness
- honest about Lucky Paws being unavailable
- aligned between code, UI state, and repo docs
