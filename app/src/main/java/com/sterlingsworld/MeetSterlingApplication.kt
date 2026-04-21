package com.sterlingsworld

import android.app.Application
import com.sterlingsworld.data.preferences.AppPreferencesRepositoryImpl
import com.sterlingsworld.data.progress.GameProgressDatabase
import com.sterlingsworld.data.progress.GameProgressRepositoryImpl
import com.sterlingsworld.domain.repository.AppPreferencesRepository
import com.sterlingsworld.domain.repository.GameProgressRepository

class MeetSterlingApplication : Application() {

    lateinit var preferencesRepository: AppPreferencesRepository
        private set

    lateinit var gameProgressRepository: GameProgressRepository
        private set

    override fun onCreate() {
        super.onCreate()

        preferencesRepository = AppPreferencesRepositoryImpl(this)

        val db = GameProgressDatabase.getInstance(this)
        gameProgressRepository = GameProgressRepositoryImpl(db.gameProgressDao())
    }
}
