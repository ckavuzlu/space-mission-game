package com.example.spacemission.database.dao

import androidx.room.*
import com.example.spacemission.database.entities.SpaceStation

@Dao
interface SpaceStationDao {

    @Query("SELECT * FROM SpaceStation")
    fun getAll(): List<SpaceStation>

    @Insert
    fun insertList(list : List<SpaceStation>)

    @Query("DELETE FROM SpaceStation")
    fun deleteAll()

    @Update(entity = SpaceStation::class)
    fun updateSpaceStation(spaceStation: SpaceStation)

    @Query("SELECT * FROM SpaceStation WHERE isFavorite = 1")
    fun getFavoriteStations(): List<SpaceStation>

}