# Next Sprint For Claude

## Objective

The next sprint is not about rescuing project basics anymore.
The project is already buildable and the shell is materially real.

The next sprint should harden the `new-ui` build, verify the newly launched attraction surfaces, and close the gap between "launchable in app" and "ship-ready for v1".

## Current checkpoint summary

What is solid:

- app structure and Gradle wrapper flow
- typed navigation shell and themed park attraction screens
- welcome/state foundations
- settings/reset foundations
- catalog migration
- native game shell and completion flow
- Media3-backed video playback for Cinema and Kidz
- packaged HTML/WebView game entry for non-native titles
- targeted unit tests for catalog and native gameplay flows

What is not acceptable yet:

- no manual QA pass against the current `new-ui` flow
- no verified device/emulator smoke test for the seven WebView-backed games
- Studio PAD runtime still not proven in a Play-installed build
- battle-family follow-on titles are live in UI but not yet promoted into the ship-ready set
- project docs were lagging behind the new UI branch until this update

## Sprint goals

Claude must complete the following in this sprint.

### 1. Re-verify build and test baseline on the promoted UI branch

Required outcomes:

- prove the promoted `new-ui` branch still builds from the project root
- rerun the targeted unit test suite that covers the current ship-ready games and catalog state
- report the exact commands used and whether they passed

Required reporting:

- exact command used
- whether it passed
- if it failed, exact blocker and file

This is still the first priority because the current PR to make `new-ui` the main build should not merge on stale build assumptions.

### 2. Run a manual QA pass on the current routed surface area

Required outcomes:

- verify the four ship-ready native games:
  - `Lucky Paws`
  - `Ghost`
  - `Cognitive Creamery`
  - `Symptom Striker`
- smoke test the seven packaged WebView-backed titles:
  - `Relaxation Retreat`
  - `Spoon Gauntlet`
  - `AOL`
  - `Kidz Doodle Land`
  - `Kidz Linebreaker`
  - `Lumi's Star Quest`
  - `Nostalgia`
- verify the attraction-to-runtime routing from the new park UI
- verify Cinema and Kidz video routing through the shared player

Required reporting:

- what was exercised
- exact failure points
- whether failures are content bugs, routing bugs, or asset-loading bugs

### 3. Validate Studio honestly

Required outcomes:

- prove whether PAD-provided Studio assets resolve in the intended install path
- confirm whether playback can be promoted from "unavailable in this build" to a real ready state
- if PAD validation still cannot be completed, keep the documentation and user-facing copy explicit about that limitation

Required reporting:

- whether the blocker is tooling, install method, asset resolution, or playback
- what exact change would be required to graduate Studio into v1

### 4. Decide the next ship-ready promotion candidate

Required outcomes:

- choose whether `AOL` or `Lumi's Star Quest` is the next title to move from WebView-backed launchable state into a native or otherwise ship-ready state
- do not start both in the same sprint
- use reuse from `Symptom Striker` where possible instead of inventing another parallel battle implementation

## Explicit non-goals for this sprint

Do not:

- add more attraction screens just for visual coverage
- expand into speculative features
- add analytics, backend, auth, or notifications
- claim Studio is solved without a verified Play-installed PAD path
- claim the WebView-backed games are v1 ship-ready without manual validation and a deliberate scope decision

## Deliverables required at end of sprint

Claude must provide:

1. Build status
   - exact working wrapper commands
   - whether `assembleDebug` works
   - whether targeted tests run

2. QA status
   - native game pass/fail notes
   - WebView game pass/fail notes
   - media routing pass/fail notes

3. Studio status
   - PAD validation result
   - playback readiness result
   - remaining blocker if still unavailable

4. Game status
   - which titles remain v1 ship-ready
   - which titles are still launchable-only
   - which title is the next promotion candidate

## Completion standard

This sprint is successful only if the promoted UI branch is materially more trustworthy at the end:

- verified by build and targeted tests
- manually exercised on the surfaced routes that now exist
- more honest about Studio readiness
- clearer about which games are truly in v1 versus only launchable in the app

The next sprint should not begin until this one produces a concrete QA and validation answer.
