package com.sterlingsworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sterlingsworld.core.di.AppContainer
import com.sterlingsworld.core.di.LocalAppContainer
import com.sterlingsworld.core.navigation.AppScaffold
import com.sterlingsworld.core.navigation.MeetSterlingNavGraph
import com.sterlingsworld.core.navigation.Screen
import com.sterlingsworld.core.ui.theme.MeetSterlingTheme
import com.sterlingsworld.feature.idle.IdleAwareRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as MeetSterlingApplication
        val appContainer = AppContainer(
            preferencesRepository = app.preferencesRepository,
            gameProgressRepository = app.gameProgressRepository,
        )

        setContent {
            MeetSterlingTheme {
                CompositionLocalProvider(LocalAppContainer provides appContainer) {
                    val navController = rememberNavController()

                    IdleAwareRoot {
                        AppScaffold(navController = navController) { innerPadding ->
                            MeetSterlingNavGraph(
                                navController = navController,
                                startDestination = Screen.Welcome.route,
                                modifier = Modifier.padding(innerPadding),
                            )
                        }
                    }
                }
            }
        }
    }
}
