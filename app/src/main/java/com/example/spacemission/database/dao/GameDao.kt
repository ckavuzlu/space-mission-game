package com.example.spacemission.database.dao

import androidx.room.*
import com.example.spacemission.database.entities.Game

@Dao
interface GameDao {
    @Query("SELECT * FROM Game")
    fun getAll(): List<Game>

    @Insert
    fun insertGame(game: Game)

    @Query("DELETE FROM Game")
    fun deleteAll()

    @Update
    fun update(game: Game)
}