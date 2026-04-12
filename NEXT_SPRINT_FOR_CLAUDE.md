# Next Sprint For Claude

## Objective

The next sprint is not about adding more surface area.
It is about making `C:\Users\Gutie\projects\android` a real, buildable, cleaner Kotlin project and eliminating the most visible fake-complete behavior.

This sprint must directly address the current blockers found in review.

## Current checkpoint summary

What is solid:

- app structure
- navigation shell
- welcome/state foundations
- settings/reset foundations
- catalog migration
- Room/DataStore foundations
- initial unit tests for catalogs

What is not acceptable yet:

- no verified Gradle build path
- missing Gradle wrapper bootstrap
- missing packaged assets
- missing launcher resources
- placeholder media playback
- placeholder game runtime
- placeholder visual assets on core screens
- leftover package debris under `com.meetsterling`
- mojibake/encoding corruption in source

## Sprint goals

Claude must complete the following in this sprint.

### 1. Restore build bootstrap and prove compilation

Required outcomes:

- add a complete Gradle wrapper to the Kotlin project:
  - `gradlew`
  - `gradlew.bat`
  - `gradle/wrapper/gradle-wrapper.jar`
- ensure the project can be built from the project root with a wrapper command
- report the exact first successful command, for example:
  - `.\gradlew.bat assembleDebug`
  - `.\gradlew.bat test`

Required reporting:

- exact command used
- whether it passed
- if it failed, exact blocker and file

This is the first priority because no build status is trustworthy until the project can compile locally through its own wrapper.

### 2. Fix project hygiene issues that can poison later work

Required outcomes:

- remove or reconcile stale Kotlin files under `app/src/main/java/com/meetsterling`
- keep only the canonical `com.sterlingsworld` package tree unless there is a very explicit justified reason not to
- fix mojibake/encoding corruption in current Kotlin source files
- ensure comments and strings are ASCII-clean unless non-ASCII is truly required

Required reporting:

- list of files removed or merged
- list of files with encoding cleanup
- whether any package/namespace mismatch remains

### 3. Make resource packaging minimally real

Required outcomes:

- add the missing launcher resources required by the manifest, or update the manifest/resources to a valid consistent state
- create the correct Android asset directory structure for the migrated content
- document where app assets now live in the Kotlin project

At minimum, this sprint must establish a real asset packaging plan in code and filesystem layout, not just catalog strings pointing at files that do not exist in the Android app.

Required reporting:

- actual asset root used
- whether map/image/video/audio files were copied yet
- what remains to be copied

### 4. Remove obvious placeholder visuals from core shell screens

Required outcomes:

- replace the placeholder map panel in `MapScreen` with the real bundled park map if the asset is available
- replace the placeholder welcome mascot box with the real bundled mascot image if the asset is available
- replace obvious thumbnail placeholder blocks in `CinemaScreen` if the asset path strategy supports real artwork now

Important:

- do not fake this with generic icons or text boxes
- if the real assets are not copied in this sprint, say so explicitly and explain what is blocked

### 5. Replace placeholder media architecture with real implementation start, not comments

Required outcomes:

- convert `StudioPlaybackService` from a no-op registered service into a real Media3-backed playback foundation
- wire `StudioScreen` to real playback state and at least real play selection behavior from the catalog
- convert `VideoPlayerScreen` from placeholder text to a real Media3/ExoPlayer-backed local player if asset packaging is ready

Important:

- do not leave “Phase 3” comments in these files after the work
- if a subsystem cannot be completed because assets are not yet packaged, then complete the real player plumbing anyway and document the exact missing asset blockers

### 6. Remove fake-complete game behavior from the current game shell

Required outcomes:

- remove the `Complete (dev)` behavior from `GameShellScreen`
- remove any UI text that presents unfinished game work inside the user-facing runtime
- replace the current shell behavior with one of these two acceptable states:
  1. a real first implemented game runtime for one concrete game, or
  2. a non-user-facing internal shell that is no longer pretending to be a playable screen

Important:

- the game screen must not remain a dressed-up placeholder
- if full game runtime work is not started in this sprint, then Claude must refactor the shell so it is an internal foundation, not a fake player-facing experience

## Explicit non-goals for this sprint

Do not:

- add more new screens just to show motion
- expand into speculative features
- add analytics/backend/auth/notifications
- leave new “Phase N” comments in core feature files
- claim media or game systems are complete if they are still UI shells

## Deliverables required at end of sprint

Claude must provide:

1. Build status
   - exact working wrapper commands
   - whether `assembleDebug` works
   - whether unit tests run

2. Cleanup status
   - stale package files removed or reconciled
   - encoding issues fixed

3. Asset/resource status
   - launcher resources status
   - asset directory status
   - map/welcome/media asset integration status

4. Media status
   - what part of Studio playback is real now
   - what part of video playback is real now
   - what remains blocked

5. Game status
   - whether the fake completion shell was removed
   - what the current real game-runtime plan is for the next sprint

## Completion standard

This sprint is successful only if the Kotlin project is materially more real at the end:

- buildable or much closer to buildable
- cleaner
- less placeholder-driven
- more honest in media/game implementation state

The next sprint should not begin until this one produces a concrete build/bootstrap answer.
