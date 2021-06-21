package com.example.spacemission.repository

import com.example.spacemission.database.dao.GameDao
import com.example.spacemission.database.dao.SpaceStationDao
import com.example.spacemission.database.entities.Game
import com.example.spacemission.model.ErrorModel
import com.example.spacemission.model.RepoResponse
import com.example.spacemission.database.entities.SpaceStation
import com.example.spacemission.database.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val spaceStationDao: SpaceStationDao,
    private val apiInterface: ApiInterface
) {


    private val channelGame = Channel<RepoResponse<Game, ErrorModel>> { }
    val flowGame: Flow<RepoResponse<Game, ErrorModel>> = channelGame.receiveAsFlow()

    private val channelSpaceStation = Channel<List<SpaceStation>> { }
    val flowSpaceStation: Flow<List<SpaceStation>> = channelSpaceStation.receiveAsFlow()

    private val channelFavSpaceStation = Channel<List<SpaceStation>> { }
    val flowFavSpaceStation: Flow<List<SpaceStation>> = channelFavSpaceStation.receiveAsFlow()

    suspend fun getGameInfo() {
        withContext(Dispatchers.IO) {
            val dbGame = gameDao.getAll()
            if (dbGame.isNotEmpty()) {
                channelGame.send(RepoResponse.Data(dbGame.last()))
                return@withContext
            } else {
                channelGame.send(RepoResponse.Error(ErrorModel("There is No Game")))
            }
        }
    }

    suspend fun insertGame(game: Game) {
        withContext(Dispatchers.IO) {
            gameDao.insertGame(game)
        }
    }

    suspend fun getStationInfo() {
        withContext(Dispatchers.IO) {
            var dbStationList = spaceStationDao.getAll()
            if (dbStationList.isEmpty()) {
                getStationInfoFromRemote()
            } else {
                channelSpaceStation.send(dbStationList)
            }
        }
    }

    private suspend fun getStationInfoFromRemote() {
        withContext(Dispatchers.IO) {
            val networkResult = apiInterface.getSpaceStations()
            if (networkResult.isSuccessful && !networkResult.body().isNullOrEmpty()) {
                insertStationList(networkResult.body()!!)
                getStationInfo()
            } else {
                channelSpaceStation.send(emptyList())
            }
        }
    }

    private suspend fun insertStationList(list: List<SpaceStation>) {
        withContext(Dispatchers.IO) {
            spaceStationDao.insertList(list)
        }
    }

    suspend fun updateStation(item: SpaceStation) {
        withContext(Dispatchers.IO) {
            spaceStationDao.updateSpaceStation(item)
            getStationInfo()
        }
    }

    suspend fun updateGame(item: Game) {
        withContext(Dispatchers.IO) {
            gameDao.update(item)
            getGameInfo()
        }
    }

    suspend fun restartGame() {
        withContext(Dispatchers.IO) {
            gameDao.deleteAll()
            spaceStationDao.deleteAll()
            getStationInfo()
        }
    }

    suspend fun getFavList() {
        withContext(Dispatchers.IO) {
            val favList = spaceStationDao.getFavoriteStations()
            channelFavSpaceStation.send(favList)
        }
    }

}