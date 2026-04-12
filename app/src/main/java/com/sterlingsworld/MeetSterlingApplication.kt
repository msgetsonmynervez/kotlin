package com.sterlingsworld

import android.app.Application
import com.sterlingsworld.data.preferences.AppPreferencesRepository
import com.sterlingsworld.data.progress.GameProgressDatabase
import com.sterlingsworld.data.progress.GameProgressRepository

class MeetSterlingApplication : Application() {

    lateinit var preferencesRepository: AppPreferencesRepository
        private set

    lateinit var gameProgressRepository: GameProgressRepository
        private set

    override fun onCreate() {
        super.onCreate()

        preferencesRepository = AppPreferencesRepository(this)

        val db = GameProgressDatabase.getInstance(this)
        gameProgressRepository = GameProgressRepository(db.gameProgressDao())
    }
}
