# AI CLI Implementation Prompt

Review `PROJECT_REVISION_REPORT.md` and implement the work in phases, preserving all existing user-visible functionality unless the report explicitly calls for a safety fix.

## Operating rules

1. Do not do a broad rewrite.
2. Prefer small, targeted, testable changes.
3. Preserve current functionality unless the report explicitly identifies a security or correctness issue that requires a behavior change.
4. Keep code changes easy to review and grouped by phase.
5. After each phase, summarize:
   - files changed
   - behavior preserved
   - risks reduced
   - remaining risks
   - verification performed

## Implementation order

### Phase 1 — safety and correctness
Start with:
1. Harden `WebViewGame`
2. Persist real settings state
3. Make progress writes atomic
4. Guard against duplicate completion events
5. Audit/remove unused microphone permission if applicable

### Phase 2 — release reliability
Then:
1. Validate/fix asset-pack media resolution
2. Add smoke tests around media availability states
3. Verify video/audio lifecycle release paths

### Phase 3 — architecture stabilization
Then:
1. Introduce `GameRegistry`
2. Reduce manual routing in `NavGraph.kt`
3. Move dependency lookup out of composables
4. Replace global media state pattern

### Phase 4 — smoothness and performance refinement
Then:
1. Standardize lifecycle-aware flow collection
2. Audit and optimize heavy image assets
3. Profile navigation and WebView entry/exit
4. Trim unnecessary recomposition scope
5. Add lightweight startup/performance measurements if practical

## Required verification

Before finishing, verify all of the following:

### Security
- `WebView` cannot browse to unexpected remote content
- JS bridge still works only for intended local assets
- backup/data extraction behavior remains restrictive
- microphone permission matches actual shipped use

### Functional behavior
- all currently playable games still launch and complete
- settings persist correctly
- studio playback still works
- video playback still works
- navigation and back-stack behavior remain correct

### Data integrity
- repeated restart/completion events do not corrupt progress counts
- existing progress survives expected app flows

### Performance and smoothness
- no new jank on tab switching
- re-entering WebView games does not leak or degrade noticeably
- media playback startup is not slower
- no obvious memory regressions during repeated navigation

## Deliverables

At the end, provide:
1. a concise changelog
2. a list of modified files
3. a verification report
4. any remaining risks or follow-up work