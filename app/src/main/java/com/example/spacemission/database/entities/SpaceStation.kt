package com.example.spacemission.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class SpaceStation(

    @PrimaryKey(autoGenerate = true) var uid: Int = 0,

    @ColumnInfo(name = "coordinateY")
    @SerializedName("coordinateY")
    val coordinateY: Double,

    @ColumnInfo(name = "coordinateX")
    @SerializedName("coordinateX")
    val coordinateX: Double,

    @ColumnInfo(name = "need")
    @SerializedName("need")
    var need: Int ,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String,

    @ColumnInfo(name = "stock")
    @SerializedName("stock")
    var stock: Int,

    @ColumnInfo(name = "capacity")
    @SerializedName("capacity")
    var capacity: Int,

    @ColumnInfo(name = "isFavorite")
    @SerializedName("isFavorite")
    var isFavorite: Boolean = false,

    @ColumnInfo(name = "distanceFromCurrent")
    @SerializedName("distanceFromCurrent")
    var distanceFromCurrent: Int? = null
)
