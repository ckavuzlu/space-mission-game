package com.example.spacemission.database

import com.example.spacemission.database.entities.SpaceStation
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET(".")
    suspend fun getSpaceStations(): Response<List<SpaceStation>>


}
