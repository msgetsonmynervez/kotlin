# Live Game Code Review
**Reviewer:** Senior Game Engineer (14 yrs shipped, not interested in your feelings)
**Branch:** `new-ui`
**Date:** 2026-04-19
**Status:** NOT APPROVED

---

## Game 1 — Lucky Paws
**Verdict: This is not a game.**

```kotlin
// LuckyPawsViewModel.buildResult()
completed = true, score = 1, stars = 3, perfect = true, durationMs = 0
```

Every single result field is hardcoded. `durationMs = 0`. The player literally cannot lose. A randomized text picker was shipped and called a game with a 3-star perfect completion. The progress repository records this as a session. The analytics system is being lied to.

**Required fix:** Either give it a mechanic (timer, streaks, miss tracking) or reclassify it as a reward screen and remove it from the arcade game list entirely. Do not call `buildResult()` with constants.

---

## Game 2 — Symptom Striker
**Verdict: Closest thing to real game logic here. Still has problems.**

### Issue 1 — Permanent spoon penalty is a run-bricking trap
```kotlin
pushThroughSafeUses: 3
// After 3 uses → lose 1 max Spoon permanently (session-wide)
```
Max Spoon drain is session-persistent and irreversible mid-run. A new player who doesn't read the warning will hit Push Through repeatedly in Gym 1 and arrive at Gym 3 unable to afford any move. There is no floor on max Spoons. Define a minimum or cap the penalty.

### Issue 2 — Vertigo at 35% miss is not tuned
A 35% miss chance on attacks with no counter-play other than a Cure move that costs Spoons is a frustration sink at Gym 3. With the push-through penalty potentially already draining Spoons, this compounds into a dead end. Nobody playtested this combination.

### Issue 3 — Score ceiling is arbitrary and unnormalized
```kotlin
score = (encounters_cleared × 100) + (won ? hp_ratio × 50 : 0)
// Max: 350
```
Max score is 350 with no documentation of why 350 is the ceiling. The completion screen presumably shows this to the player raw. Either normalize to 0–1000 or document the scale. "350" communicates nothing.

### Issue 4 — Rage trigger has no visual tell
`rageTriggerPercent = 0.30` triggers enemy rage at 30% HP. If there is no animation or clear UI indicator, the `1.5× damage` spike will feel like a bug to the player, not a mechanic.

---

## Game 3 — Cognitive Creamery
**Verdict: Brain Freeze loop is broken. Stars logic is wrong.**

### Issue 1 — Brain Freeze has no real consequence
```
fatigue ≥ 5 → Brain Freeze overlay → Reset → fatigue = 0 → repeat forever
```
The player can fail indefinitely and reset with zero penalty. Brain Freeze shows 1 star but the player can ignore the score. If the intent is punishment, gate the reset behind a cooldown or a stat deduction. If the intent is accessibility, that is fine, but do not call it a consequence.

### Issue 2 — Stars formula references the wrong game
Stars logic: `3 if perfect sequence run, 2 if session score ≥ 2, else 1`. Cognitive Creamery's Clarity mode contributes to `sessionScore` but has no sequence mechanic. A player who plays only Clarity and wins every round still gets 2 stars at best because the "perfect" check requires a clean Sequence run. This is a copy-paste error from the wrong game's result builder.

### Issue 3 — Sequence round lengths are static
Rounds are hardcoded `[3, 4, 5]` items with no difficulty scaling on repeat play. A player returning for a second session gets the identical challenge. No adaptive difficulty, no seed variation.

### Issue 4 — Undo behavior is unspecified
The Sequence game has an Undo button but the code does not specify whether it refunds the fatigue hit if a wrong token was placed. If it does not, players will avoid using it. If it does, it trivializes the challenge. Either decision is fine — it needs to be explicit and documented in the code.

---

## Game 4 — Spoon Gauntlet
**Verdict: Unreviewed black box. Incomplete submission.**

The entire game is a native activity (`MyelinProtocolActivity`) with no visible Kotlin game logic in this repository. Code that cannot be read cannot be approved. If the native implementation lives in a separate module or library, that module needs to be in scope for this review. The Compose layer is a Play button and a background image.

---

## Game 5 — Relaxation Retreat
**Verdict: 5 of 6 features are stubs. Should not be listed as a live game.**

```kotlin
// RelaxationSuiteHost.kt
WORDLE    -> HealthWordleGame()     // placeholder
SUDOKU    -> IslandSudokuGame()     // placeholder
CROSSWORD -> MsCrosswordGame()      // placeholder
SOLITAIRE -> IslandSolitaireGame()  // placeholder
MATCH3    -> TropicalMatch3Game()   // placeholder
```

Five of six tabs call stub functions with no implementation. That is 83% of the advertised feature set missing. The Trivia game — the one implemented activity — has 15 questions total. Fifteen. That content pool exhausts in a single sitting with repeats appearing the next session.

**Required:** Gate the suite behind a feature flag or remove the empty tabs from the UI until they ship. Do not show players a tab row with five broken entries.

### Secondary issue — Trivia question pool attribution
Questions about MS definition, brain weight, and nervous system function are medical-adjacent. They need a medical review sign-off before they reach patients. Document who approved that content.

---

## Game 6 — AOL (Armor of Light)
**Verdict: HTML file in an asset folder. Game logic unauditable from this review.**

The game logic lives entirely in `assets/games/AOL/index.html` and its JS. The Kotlin layer is a sandboxed WebView wrapper — that part is correctly implemented. However `ship_ready` is not set in `GameCatalog` while `isLive = true` in the arcade screen. Those two flags are in conflict. Pick one source of truth and remove the other.

The claim that "AOL reuses BattleModel shared with SymptomStriker" is impossible. A WebView HTML game cannot reference a Kotlin data class at runtime. This is either documentation fiction or there is a duplicated data model between the HTML game and the Kotlin game with no shared source of truth. Either way it is a maintenance problem waiting to cause a divergence bug.

---

## Cross-Cutting Issues

### 1 — `AtomicBoolean` completion guard in coroutine context
```kotlin
// GameShellViewModel
private val completionRecorded = AtomicBoolean(false)
```
`AtomicBoolean` is a Java threading primitive. In a coroutine-based ViewModel the correct tool is a `Mutex` or a single-writer `StateFlow`. This is not catastrophically wrong but it signals a paradigm switch mid-file and will confuse the next engineer who touches it.

### 2 — No loss path recorded in analytics
`GameProgressRepository.recordCompletion()` is called on exit regardless of actual result. A 0-star DNF and a 3-star perfect run are indistinguishable in the data when `buildResult()` returns the same hardcoded values (LuckyPaws) or when the completion event fires on any exit.

### 3 — Zero unit tests visible in scope
`SymptomStrikerViewModel` has a `companion object { fun test(...) }` factory — correct instinct — but no test files were found in scope. That factory exists to be tested. Use it.

---

## Approval Checklist

Fix every item before this branch merges.

- [ ] **LuckyPaws** — Real mechanic or demote to reward screen; remove hardcoded `buildResult()` constants
- [ ] **SymptomStriker** — Minimum Spoon floor; Vertigo tuning pass; rage visual tell; normalize score to documented ceiling
- [ ] **Creamery** — Brain Freeze consequence; fix stars logic for Clarity-only sessions; document Undo fatigue behavior
- [ ] **Spoon Gauntlet** — Native module in review scope, or written exception signed off by native team lead
- [ ] **Relaxation Retreat** — Hide unimplemented tabs behind feature flag; expand Trivia pool to minimum 50 questions; medical content sign-off documented
- [ ] **AOL** — Reconcile `ship_ready` vs `isLive` flag conflict; clarify or correct BattleModel sharing claim
- [ ] **Global** — Replace `AtomicBoolean` with `Mutex`; wire real completion outcome data into analytics for all games
