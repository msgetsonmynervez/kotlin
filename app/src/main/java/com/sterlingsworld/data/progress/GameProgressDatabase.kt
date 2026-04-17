package com.sterlingsworld.data.progress

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GameProgressEntity::class], version = 1, exportSchema = false)
abstract class GameProgressDatabase : RoomDatabase() {

    abstract fun gameProgressDao(): GameProgressDao

    companion object {
        @Volatile private var INSTANCE: GameProgressDatabase? = null

        fun getInstance(context: Context): GameProgressDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    GameProgressDatabase::class.java,
                    "game_progress.db",
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
