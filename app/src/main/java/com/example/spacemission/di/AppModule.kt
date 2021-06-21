package com.example.spacemission.di

import android.content.Context
import androidx.room.Room
import com.example.spacemission.database.AppDatabase
import com.example.spacemission.database.dao.GameDao
import com.example.spacemission.database.dao.SpaceStationDao
import com.example.spacemission.database.ApiInterface
import com.example.spacemission.repository.GameRepository
import com.example.spacemission.util.Constants.BASE_URL
import com.example.spacemission.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getRetrofitApi(): ApiInterface = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)

    @Singleton
    @Provides
    fun getAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun getGameDao(appDatabase: AppDatabase): GameDao = appDatabase.gameDao()

    @Singleton
    @Provides
    fun getSpaceStationDao(appDatabase: AppDatabase): SpaceStationDao = appDatabase.spaceStationDao()

    @Singleton
    @Provides
    fun getRepo(gamedao: GameDao, spaceStationDao: SpaceStationDao, apiInterface: ApiInterface): GameRepository = GameRepository(gamedao, spaceStationDao, apiInterface)

}