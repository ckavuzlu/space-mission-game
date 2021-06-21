package com.example.spacemission.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0,

    @ColumnInfo(name = "durability")
    var durability: Float,

    @ColumnInfo(name = "storage")
    var storage: Float,

    @ColumnInfo(name = "speed")
    var speed: Float,

    @ColumnInfo(name = "spaceshipName")
    val spaceshipName: String,

    @ColumnInfo(name = "damage_capacity")
    var damageCapacity: Int = 100,

    @ColumnInfo(name = "current_location")
    var currentLocation: String
)
