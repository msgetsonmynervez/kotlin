package com.sterlingsworld.feature.game.games.cognitivecreamery

import androidx.lifecycle.ViewModel
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

const val CREAMERY_MAX_LEVEL = 50
const val CREAMERY_MAX_FATIGUE = 100

enum class CreameryActivity(
    val label: String,
    val badge: String,
    val description: String,
    val accent: Long,
) {
    PARLOR(
        label = "Midnight Parlor",
        badge = "PARLOR",
        description = "Choose a calm focus activity and play at your own pace.",
        accent = 0xFFD6AE63,
    ),
    CLARITY(
        label = "Cognitive Clarity",
        badge = "CLR",
        description = "Find the three words linked to the theme.",
        accent = 0xFF8BCFD6,
    ),
    SCAN(
        label = "Signal Scan",
        badge = "SCN",
        description = "Locate the target treats hidden in the crowd.",
        accent = 0xFFD29E63,
    ),
    SEQUENCE(
        label = "Sweet Sequence",
        badge = "SEQ",
        description = "Watch the candy order, then repeat it exactly.",
        accent = 0xFFB8C97F,
    ),
    CATEGORY(
        label = "Candy Categorizer",
        badge = "CAT",
        description = "Sort treats by shifting rules and category targets.",
        accent = 0xFFD7945F,
    ),
    SYMMETRY(
        label = "Sugar Symmetry",
        badge = "SYM",
        description = "Match the blueprint by cycling each tile into place.",
        accent = 0xFF8C7AA9,
    ),
    FLIP(
        label = "Flavor Flip",
        badge = "FLP",
        description = "Choose the color or the word under changing rules.",
        accent = 0xFFB56A5C,
    ),
    PATTERN(
        label = "Pattern Pieces",
        badge = "PAT",
        description = "Complete the missing treat in the logical pattern.",
        accent = 0xFFA3E635,
    ),
}

data class CreameryResultOverlay(
    val activity: CreameryActivity,
    val title: String,
    val message: String,
    val success: Boolean,
)

data class ClarityRound(
    val theme: String = "",
    val answers: List<String> = emptyList(),
    val options: List<String> = emptyList(),
    val foundWords: Set<String> = emptySet(),
    val wrongWords: Set<String> = emptySet(),
)

data class ScanRound(
    val target: String = "",
    val needed: Int = 0,
    val grid: List<String> = emptyList(),
    val foundIndices: Set<Int> = emptySet(),
    val wrongIndices: Set<Int> = emptySet(),
)

enum class SequencePhase { STUDY, INPUT, ROUND_RESULT }

data class SequenceRound(
    val phase: SequencePhase = SequencePhase.STUDY,
    val targetSequence: List<String> = emptyList(),
    val choices: List<String> = emptyList(),
    val playerSequence: List<String> = emptyList(),
    val lastRoundCorrect: Boolean? = null,
)

data class CategoryItem(val symbol: String, val categories: Set<String>)

data class CategoryRound(
    val targetCategory: String = "",
    val items: List<CategoryItem> = emptyList(),
    val remaining: Int = 0,
    val matchedIndices: Set<Int> = emptySet(),
    val wrongIndices: Set<Int> = emptySet(),
)

data class SymmetryRound(
    val cellCount: Int = 4,
    val blueprint: List<Int> = emptyList(),
    val player: List<Int> = emptyList(),
)

data class FlipRound(
    val rule: String = "COLOR",
    val displayWord: String = "",
    val displayColorName: String = "",
    val targetAnswer: String = "",
    val displayColor: Long = 0xFFFFFFFF,
    val answerLabels: List<Pair<String, Long>> = emptyList(),
)

data class PatternRound(
    val sequence: List<String> = emptyList(),
    val correct: String = "",
    val options: List<String> = emptyList(),
)

data class CognitiveCreameryUiState(
    val currentActivity: CreameryActivity = CreameryActivity.PARLOR,
    val flavorOfTheDay: CreameryActivity = CreameryActivity.CLARITY,
    val activeLevel: Int = 1,
    val activeFatigue: Int = 0,
    val clearedModes: Set<CreameryActivity> = emptySet(),
    val resultOverlay: CreameryResultOverlay? = null,
    val clarity: ClarityRound = ClarityRound(),
    val scan: ScanRound = ScanRound(),
    val sequence: SequenceRound = SequenceRound(),
    val category: CategoryRound = CategoryRound(),
    val symmetry: SymmetryRound = SymmetryRound(),
    val flip: FlipRound = FlipRound(),
    val pattern: PatternRound = PatternRound(),
)

private data class ModeProgress(
    val level: Int = 1,
    val fatigue: Int = 0,
)

private val PLAYABLE_ACTIVITIES = listOf(
    CreameryActivity.CLARITY,
    CreameryActivity.SCAN,
    CreameryActivity.SEQUENCE,
    CreameryActivity.CATEGORY,
    CreameryActivity.SYMMETRY,
    CreameryActivity.FLIP,
    CreameryActivity.PATTERN,
)

private data class ClarityTheme(val name: String, val answers: List<String>, val distractors: List<String>)

private val CLARITY_THEMES = listOf(
    ClarityTheme("Library", listOf("Book", "Quiet", "Shelf"), listOf("Pizza", "Sand", "Drum")),
    ClarityTheme("Winter", listOf("Snow", "Scarf", "Ice"), listOf("Heat", "Beach", "Sun")),
    ClarityTheme("Ocean", listOf("Salt", "Wave", "Whale"), listOf("Dust", "Tree", "Road")),
    ClarityTheme("Kitchen", listOf("Oven", "Fork", "Plate"), listOf("Cloud", "Grass", "Soap")),
    ClarityTheme("Space", listOf("Star", "Planet", "Orbit"), listOf("Carpet", "Socks", "Fence")),
)

private val SCAN_SYMBOLS = listOf("🍦", "🧁", "🍩", "🍪", "🍭", "🍒", "🍓", "🍎")
private val SEQUENCE_TOKENS = listOf("🍬", "🍭", "🍩", "🧁", "🍫", "🍪", "🍓", "🍒", "🍨")
private val CANDY_CATEGORIES = listOf("Red", "Fruit", "Bakery", "Candy", "Cold")
private val CATEGORY_ITEMS = listOf(
    CategoryItem("🍎", setOf("Red", "Fruit")),
    CategoryItem("🍒", setOf("Red", "Fruit")),
    CategoryItem("🍓", setOf("Red", "Fruit")),
    CategoryItem("🍩", setOf("Bakery")),
    CategoryItem("🍪", setOf("Bakery")),
    CategoryItem("🧁", setOf("Bakery")),
    CategoryItem("🍫", setOf("Candy")),
    CategoryItem("🍭", setOf("Candy")),
    CategoryItem("🍬", setOf("Candy")),
    CategoryItem("🍦", setOf("Cold")),
    CategoryItem("🍨", setOf("Cold")),
    CategoryItem("🧊", setOf("Cold")),
)
private val SYMMETRY_TILES = listOf("", "🍭", "🍩", "🧁")
private val FLIP_CHOICES = listOf(
    "MINT" to 0xFF00FFAA,
    "CHERRY" to 0xFFFF0055,
    "BLUE" to 0xFF00AAFF,
    "LEMON" to 0xFFFFCC00,
)
private val PATTERN_TREATS = listOf("🍦", "🍩", "🧁", "🍪", "🍭", "🍬", "🍒", "🍓")

class CognitiveCreameryViewModel : ViewModel() {

    private val startTimeMs = System.currentTimeMillis()
    private val progress = PLAYABLE_ACTIVITIES.associateWith { ModeProgress() }.toMutableMap()
    private val clearedModes = linkedSetOf<CreameryActivity>()

    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<CognitiveCreameryUiState> = _uiState.asStateFlow()

    fun navigateTo(activity: CreameryActivity) {
        if (activity == CreameryActivity.PARLOR) {
            backToParlor()
            return
        }
        val mode = progress.getValue(activity)
        _uiState.value = _uiState.value.copy(
            currentActivity = activity,
            activeLevel = mode.level,
            activeFatigue = mode.fatigue,
            resultOverlay = null,
            clarity = if (activity == CreameryActivity.CLARITY) buildClarityRound() else _uiState.value.clarity,
            scan = if (activity == CreameryActivity.SCAN) buildScanRound(mode.level) else _uiState.value.scan,
            sequence = if (activity == CreameryActivity.SEQUENCE) buildSequenceRound(mode.level) else _uiState.value.sequence,
            category = if (activity == CreameryActivity.CATEGORY) buildCategoryRound() else _uiState.value.category,
            symmetry = if (activity == CreameryActivity.SYMMETRY) buildSymmetryRound(mode.level) else _uiState.value.symmetry,
            flip = if (activity == CreameryActivity.FLIP) buildFlipRound() else _uiState.value.flip,
            pattern = if (activity == CreameryActivity.PATTERN) buildPatternRound(mode.level) else _uiState.value.pattern,
        )
    }

    fun backToParlor() {
        _uiState.value = _uiState.value.copy(
            currentActivity = CreameryActivity.PARLOR,
            flavorOfTheDay = PLAYABLE_ACTIVITIES.random(),
            resultOverlay = null,
            activeLevel = 1,
            activeFatigue = 0,
            clearedModes = clearedModes.toSet(),
        )
    }

    fun dismissResultOverlay() {
        backToParlor()
    }

    fun resetSession() {
        progress.keys.forEach { progress[it] = ModeProgress() }
        clearedModes.clear()
        _uiState.value = buildInitialState()
    }

    fun onClarityWordTapped(word: String) {
        if (_uiState.value.currentActivity != CreameryActivity.CLARITY || _uiState.value.resultOverlay != null) return
        val round = _uiState.value.clarity
        if (word in round.foundWords || word in round.wrongWords) return
        if (word in round.answers) {
            val found = round.foundWords + word
            _uiState.value = _uiState.value.copy(clarity = round.copy(foundWords = found))
            if (found.size == round.answers.size) {
                completeLevel(
                    activity = CreameryActivity.CLARITY,
                    title = "Clarity Achieved!",
                    successMessage = "Semantic links restored for level ${currentMode().level}.",
                    nextState = { copy(clarity = buildClarityRound()) },
                )
            }
        } else {
            failAttempt(
                activity = CreameryActivity.CLARITY,
                fatigueDelta = 15,
                update = { copy(clarity = round.copy(wrongWords = round.wrongWords + word)) },
                failureTitle = "Fatigue Reached",
                failureMessage = "Take a rest before your next clarity session.",
            )
        }
    }

    fun onScanTapped(index: Int) {
        if (_uiState.value.currentActivity != CreameryActivity.SCAN || _uiState.value.resultOverlay != null) return
        val round = _uiState.value.scan
        if (index !in round.grid.indices || index in round.foundIndices || index in round.wrongIndices) return
        if (round.grid[index] == round.target) {
            val found = round.foundIndices + index
            _uiState.value = _uiState.value.copy(scan = round.copy(foundIndices = found))
            if (found.size == round.needed) {
                completeLevel(
                    activity = CreameryActivity.SCAN,
                    title = "Sugar Rush!",
                    successMessage = "All targets found on level ${currentMode().level}.",
                    nextState = { copy(scan = buildScanRound(nextLevelFor(CreameryActivity.SCAN))) },
                )
            }
        } else {
            failAttempt(
                activity = CreameryActivity.SCAN,
                fatigueDelta = 15,
                update = { copy(scan = round.copy(wrongIndices = round.wrongIndices + index)) },
                failureTitle = "Brain Freeze",
                failureMessage = "Rest your eyes before the next scan.",
            )
        }
    }

    fun onReadySequence() {
        if (_uiState.value.currentActivity != CreameryActivity.SEQUENCE || _uiState.value.resultOverlay != null) return
        val round = _uiState.value.sequence
        if (round.phase != SequencePhase.STUDY) return
        _uiState.value = _uiState.value.copy(
            sequence = round.copy(
                phase = SequencePhase.INPUT,
                choices = SEQUENCE_TOKENS.shuffled(),
                playerSequence = emptyList(),
                lastRoundCorrect = null,
            ),
        )
    }

    fun onSequenceTokenTapped(token: String) {
        if (_uiState.value.currentActivity != CreameryActivity.SEQUENCE || _uiState.value.resultOverlay != null) return
        val round = _uiState.value.sequence
        if (round.phase != SequencePhase.INPUT) return
        
        val currentIndex = round.playerSequence.size
        if (currentIndex >= round.targetSequence.size) return

        if (token == round.targetSequence[currentIndex]) {
            val newSequence = round.playerSequence + token
            _uiState.value = _uiState.value.copy(
                sequence = round.copy(playerSequence = newSequence)
            )
            if (newSequence.size == round.targetSequence.size) {
                completeLevel(
                    activity = CreameryActivity.SEQUENCE,
                    title = "Memory Master!",
                    successMessage = "Sequence recalled.",
                    nextState = { copy(sequence = buildSequenceRound(nextLevelFor(CreameryActivity.SEQUENCE))) }
                )
            }
        } else {
            // Wrong tap: Immediate fatigue hit and reset current input
            failAttempt(
                activity = CreameryActivity.SEQUENCE,
                fatigueDelta = 20,
                update = { copy(sequence = round.copy(playerSequence = emptyList())) },
                failureTitle = "Memory Strain",
                failureMessage = "Pattern lost. Starting over.",
            )
        }
    }

    fun onSequenceUndo() {
        // No longer used in live validation
    }

    fun onSequenceCheck() {
        // No longer used in live validation
    }

    fun onCategoryTapped(index: Int) {
        if (_uiState.value.currentActivity != CreameryActivity.CATEGORY || _uiState.value.resultOverlay != null) return
        val round = _uiState.value.category
        if (index !in round.items.indices || index in round.matchedIndices || index in round.wrongIndices) return
        val matches = round.targetCategory in round.items[index].categories
        if (matches) {
            val matched = round.matchedIndices + index
            _uiState.value = _uiState.value.copy(category = round.copy(matchedIndices = matched))
            if (matched.size == round.remaining) {
                completeLevel(
                    activity = CreameryActivity.CATEGORY,
                    title = "Flexibility Master!",
                    successMessage = "Rule shift handled on level ${currentMode().level}.",
                    nextState = { copy(category = buildCategoryRound()) },
                )
            }
        } else {
            failAttempt(
                activity = CreameryActivity.CATEGORY,
                fatigueDelta = 15,
                update = { copy(category = round.copy(wrongIndices = round.wrongIndices + index)) },
                failureTitle = "Rule Confusion",
                failureMessage = "Take a short rest before sorting again.",
            )
        }
    }

    fun onSymmetryCycle(index: Int) {
        if (_uiState.value.currentActivity != CreameryActivity.SYMMETRY || _uiState.value.resultOverlay != null) return
        val round = _uiState.value.symmetry
        if (index !in round.player.indices) return
        val nextPlayer = round.player.toMutableList()
        nextPlayer[index] = (nextPlayer[index] + 1) % SYMMETRY_TILES.size

        _uiState.value = _uiState.value.copy(symmetry = round.copy(player = nextPlayer))
    }

    fun onSymmetryCheck() {
        if (_uiState.value.currentActivity != CreameryActivity.SYMMETRY || _uiState.value.resultOverlay != null) return
        val round = _uiState.value.symmetry
        if (round.player == round.blueprint) {
            completeLevel(
                activity = CreameryActivity.SYMMETRY,
                title = "Perfect Mirror!",
                successMessage = "Blueprint mapped.",
                nextState = { copy(symmetry = buildSymmetryRound(nextLevelFor(CreameryActivity.SYMMETRY))) },
            )
        } else {
            failAttempt(
                activity = CreameryActivity.SYMMETRY,
                fatigueDelta = 20,
                update = { this },
                failureTitle = "Spatial Fatigue",
                failureMessage = "Rest your eyes.",
            )
        }
    }

    fun onFlipGuess(answer: String) {
        if (_uiState.value.currentActivity != CreameryActivity.FLIP || _uiState.value.resultOverlay != null) return
        val round = _uiState.value.flip
        if (answer == round.targetAnswer) {
            completeLevel(
                activity = CreameryActivity.FLIP,
                title = "Iron Focus!",
                successMessage = "Interference defeated on level ${currentMode().level}.",
                nextState = { copy(flip = buildFlipRound()) },
            )
        } else {
            failAttempt(
                activity = CreameryActivity.FLIP,
                fatigueDelta = 20,
                update = { this },
                failureTitle = "Interference Overload",
                failureMessage = "Brain melt reached. Take a breather.",
            )
        }
    }

    fun onPatternGuess(answer: String) {
        if (_uiState.value.currentActivity != CreameryActivity.PATTERN || _uiState.value.resultOverlay != null) return
        val round = _uiState.value.pattern
        if (answer == round.correct) {
            completeLevel(
                activity = CreameryActivity.PATTERN,
                title = "Logic Legend!",
                successMessage = "Pattern solved on level ${currentMode().level}.",
                nextState = { copy(pattern = buildPatternRound(nextLevelFor(CreameryActivity.PATTERN))) },
            )
        } else {
            failAttempt(
                activity = CreameryActivity.PATTERN,
                fatigueDelta = 20,
                update = { this },
                failureTitle = "Logic Drain",
                failureMessage = "Take a breather before the next pattern.",
            )
        }
    }

    fun buildResult(): GameResult {
        val active = _uiState.value.currentActivity
        val activeProgress = if (active == CreameryActivity.PARLOR) null else progress[active]
        val score = clearedModes.size * CREAMERY_MAX_LEVEL + (activeProgress?.level ?: 1) - 1
        val stars = when {
            clearedModes.size >= 5 -> 3
            clearedModes.isNotEmpty() || score >= 25 -> 2
            else -> 1
        }
        return GameResult(
            completed = true,
            score = score,
            stars = stars,
            durationMs = System.currentTimeMillis() - startTimeMs,
            perfect = clearedModes.size == PLAYABLE_ACTIVITIES.size,
        )
    }

    private fun failAttempt(
        activity: CreameryActivity,
        fatigueDelta: Int,
        update: CognitiveCreameryUiState.() -> CognitiveCreameryUiState,
        failureTitle: String,
        failureMessage: String,
    ) {
        val mode = currentMode(activity)
        val newFatigue = (mode.fatigue + fatigueDelta).coerceAtMost(CREAMERY_MAX_FATIGUE)
        
        if (newFatigue >= CREAMERY_MAX_FATIGUE) {
            // AAA Hardcore: Reset to Level 1 on Brain Freeze
            progress[activity] = ModeProgress(level = 1, fatigue = 0)
            _uiState.value = _uiState.value.update().copy(
                activeFatigue = 100,
                resultOverlay = CreameryResultOverlay(activity, failureTitle, failureMessage, success = false)
            )
        } else {
            progress[activity] = mode.copy(fatigue = newFatigue)
            _uiState.value = _uiState.value.update().copy(activeFatigue = newFatigue)
        }
    }

    private fun completeLevel(
        activity: CreameryActivity,
        title: String,
        successMessage: String,
        nextState: CognitiveCreameryUiState.() -> CognitiveCreameryUiState,
        updateProgressFirst: Boolean = true,
    ) {
        val mode = currentMode(activity)
        val nextLevel = mode.level + 1
        val completedAll = nextLevel > CREAMERY_MAX_LEVEL
        
        // Intermediate levels reset fatigue to 0 to keep the "calm focus" loop
        progress[activity] = if (completedAll) ModeProgress(CREAMERY_MAX_LEVEL, 0) else mode.copy(level = nextLevel, fatigue = 0)
        
        if (completedAll) clearedModes += activity
        
        var next = _uiState.value.copy(clearedModes = clearedModes.toSet())
        if (!completedAll) {
            next = next.nextState()
        }

        // AAA Flow: Skip overlay for intermediate levels
        if (completedAll) {
            next = next.copy(
                activeLevel = progress.getValue(activity).level,
                activeFatigue = 0,
                resultOverlay = CreameryResultOverlay(
                    activity = activity,
                    title = title,
                    message = completionMessage(activity),
                    success = true,
                ),
            )
        } else {
            next = next.copy(
                activeLevel = progress.getValue(activity).level,
                activeFatigue = 0,
                resultOverlay = null // Auto-advance
            )
        }
        _uiState.value = next
    }

    private fun completionMessage(activity: CreameryActivity): String = when (activity) {
        CreameryActivity.CLARITY -> "All 50 semantic links restored."
        CreameryActivity.SCAN -> "All 50 scan boards cleared."
        CreameryActivity.SEQUENCE -> "All 50 sequences recalled."
        CreameryActivity.CATEGORY -> "All 50 sorting shifts handled."
        CreameryActivity.SYMMETRY -> "All 50 blueprints mirrored."
        CreameryActivity.FLIP -> "All 50 Stroop rounds conquered."
        CreameryActivity.PATTERN -> "All 50 logic patterns solved."
        CreameryActivity.PARLOR -> ""
    }

    private fun currentMode(activity: CreameryActivity = _uiState.value.currentActivity): ModeProgress =
        progress.getValue(activity)

    private fun nextLevelFor(activity: CreameryActivity): Int = progress.getValue(activity).level

    private fun buildInitialState(): CognitiveCreameryUiState = CognitiveCreameryUiState(
        currentActivity = CreameryActivity.PARLOR,
        flavorOfTheDay = PLAYABLE_ACTIVITIES.random(),
        activeLevel = 1,
        activeFatigue = 0,
        clearedModes = emptySet(),
        clarity = buildClarityRound(),
        scan = buildScanRound(1),
        sequence = buildSequenceRound(1),
        category = buildCategoryRound(),
        symmetry = buildSymmetryRound(1),
        flip = buildFlipRound(),
        pattern = buildPatternRound(1),
    )

    companion object {
        fun buildClarityRound(): ClarityRound {
            val theme = CLARITY_THEMES.random()
            return ClarityRound(
                theme = theme.name,
                answers = theme.answers,
                options = (theme.answers + theme.distractors).shuffled(),
            )
        }

        fun buildScanRound(level: Int): ScanRound {
            val target = SCAN_SYMBOLS.random()
            val needed = Random.nextInt(2, 5)
            val grid = MutableList(16) { "" }
            var placed = 0
            while (placed < needed) {
                val idx = Random.nextInt(grid.size)
                if (grid[idx].isEmpty()) {
                    grid[idx] = target
                    placed++
                }
            }
            repeat(grid.size) { index ->
                if (grid[index].isEmpty()) {
                    grid[index] = SCAN_SYMBOLS.filter { it != target }.random()
                }
            }
            return ScanRound(target = target, needed = needed, grid = grid)
        }

        fun buildSequenceRound(level: Int): SequenceRound {
            val length = 2 + ((level - 1) / 10).coerceAtMost(4)
            return SequenceRound(
                phase = SequencePhase.STUDY,
                targetSequence = List(length) { SEQUENCE_TOKENS.random() },
                choices = emptyList(),
                playerSequence = emptyList(),
            )
        }

        fun buildCategoryRound(): CategoryRound {
            val target = CANDY_CATEGORIES.random()
            val valid = CATEGORY_ITEMS.filter { target in it.categories }.shuffled().take(3)
            val invalid = CATEGORY_ITEMS.filter { target !in it.categories }.shuffled().take(6)
            return CategoryRound(
                targetCategory = target,
                items = (valid + invalid).shuffled(),
                remaining = valid.size,
            )
        }

        fun buildSymmetryRound(level: Int): SymmetryRound {
            val count = if (level > 20) 6 else 4
            val blueprint = List(count) { Random.nextInt(1, SYMMETRY_TILES.size) }
            return SymmetryRound(
                cellCount = count,
                blueprint = blueprint,
                player = List(count) { 0 },
            )
        }

        fun buildFlipRound(): FlipRound {
            val rule = if (Random.nextBoolean()) "COLOR" else "WORD"
            val word = FLIP_CHOICES.random()
            var color = FLIP_CHOICES.random()
            if (Random.nextFloat() < 0.7f && color == word) {
                color = FLIP_CHOICES.first { it != word }
            }
            return FlipRound(
                rule = rule,
                displayWord = word.first,
                displayColorName = color.first,
                targetAnswer = if (rule == "COLOR") color.first else word.first,
                displayColor = color.second.toLong(),
                answerLabels = FLIP_CHOICES.map { it.first to it.second.toLong() },
            )
        }

        fun buildPatternRound(level: Int): PatternRound {
            val first = PATTERN_TREATS.random()
            val second = PATTERN_TREATS.filter { it != first }.random()
            val type = if (level < 10) 0 else Random.nextInt(3)
            val sequence = when (type) {
                0 -> listOf(first, second, first, second)
                1 -> listOf(first, first, second, second)
                else -> listOf(first, second, second, first)
            }
            val correct = when (type) {
                0 -> first
                1 -> first
                else -> second
            }
            val options = buildList {
                add(correct)
                while (size < 4) {
                    val next = PATTERN_TREATS.random()
                    if (next !in this) add(next)
                }
            }.shuffled()
            return PatternRound(sequence = sequence, correct = correct, options = options)
        }
    }
}
