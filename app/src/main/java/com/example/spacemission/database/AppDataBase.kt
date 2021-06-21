package com.example.spacemission.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spacemission.database.dao.GameDao
import com.example.spacemission.database.dao.SpaceStationDao
import com.example.spacemission.database.entities.SpaceStation
import com.example.spacemission.database.entities.Game

@Database(entities = arrayOf(Game::class, SpaceStation::class), version = 17, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun spaceStationDao(): SpaceStationDao
}