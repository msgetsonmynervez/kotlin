package com.sterlingsworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.sterlingsworld.core.navigation.MeetSterlingNavGraph
import com.sterlingsworld.core.navigation.Screen
import com.sterlingsworld.core.ui.theme.MeetSterlingTheme
import com.sterlingsworld.feature.idle.IdleAwareRoot
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import com.sterlingsworld.core.util.localDateStamp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as MeetSterlingApplication
        val prefs = app.preferencesRepository

        setContent {
            MeetSterlingTheme {
                val lastSeen by prefs.welcomeLastSeenDate.collectAsState(initial = null)

                // Determine start destination:
                // Show Welcome if this is the first launch today; skip to Park on subsequent opens same day.
                val startDestination = remember(lastSeen) {
                    if (lastSeen == localDateStamp()) Screen.Park.route else Screen.Welcome.route
                }

                val navController = rememberNavController()

                IdleAwareRoot {
                    MeetSterlingNavGraph(
                        navController = navController,
                        startDestination = startDestination,
                    )
                }
            }
        }
    }
}
