package com.sterlingsworld.feature.game.suites.relaxation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.domain.model.GameResult

private val BeachSand = Color(0xFFFFF8E1)
private val TropicalTeal = Color(0xFF00897B)

@Composable
fun RelaxationSuiteHost(
    onComplete: (GameResult) -> Unit,
    vm: RelaxationSuiteViewModel = viewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val activities = RelaxationActivity.entries
    val selectedIndex = activities.indexOf(uiState.currentActivity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BeachSand),
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            containerColor = BeachSand,
            contentColor = TropicalTeal,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedIndex])
                        .fillMaxWidth()
                        .height(3.dp)
                        .padding(horizontal = 16.dp)
                        .background(TropicalTeal, RoundedCornerShape(50)),
                )
            },
        ) {
            activities.forEachIndexed { index, activity ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { vm.selectActivity(activity) },
                    text = {
                        Text(
                            text = activity.label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    selectedContentColor = TropicalTeal,
                    unselectedContentColor = Color(0xFF80A89A),
                )
            }
        }

        AnimatedContent(
            targetState = uiState.currentActivity,
            transitionSpec = {
                val dir = if (targetState.ordinal > initialState.ordinal) 1 else -1
                val enter = fadeIn(tween(300)) + slideInHorizontally(tween(300)) {  dir * it / 3 }
                val exit  = fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -dir * it / 3 }
                enter.togetherWith(exit)
            },
            label = "relaxation_activity_transition",
            modifier = Modifier.weight(1f),
        ) { activity ->
            when (activity) {
                RelaxationActivity.TRIVIA -> {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        NervousSystemTriviaGame(
                            uiState = uiState.trivia,
                            onSelectAnswer = vm::selectAnswer,
                            onNext = vm::nextQuestion,
                            onFinish = { onComplete(vm.buildResult()) },
                            onRestart = vm::startTrivia,
                        )
                    }
                }
                RelaxationActivity.WORDLE     -> HealthWordleGame()
                RelaxationActivity.SUDOKU     -> IslandSudokuGame()
                RelaxationActivity.CROSSWORD  -> MsCrosswordGame()
                RelaxationActivity.SOLITAIRE  -> IslandSolitaireGame()
                RelaxationActivity.MATCH3     -> TropicalMatch3Game()
            }
        }
    }
}
