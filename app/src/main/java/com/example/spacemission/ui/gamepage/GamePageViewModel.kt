package com.example.spacemission.ui.gamepage

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacemission.database.entities.Game
import com.example.spacemission.model.ErrorModel
import com.example.spacemission.model.RepoResponse
import com.example.spacemission.database.entities.SpaceStation
import com.example.spacemission.model.GameResult
import com.example.spacemission.model.UIState
import com.example.spacemission.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class GamePageViewModel @Inject constructor(private val repository: GameRepository) : ViewModel() {

    private val _gameLiveData: MutableLiveData<Game> = MutableLiveData()
    val gameLiveData: LiveData<Game> = _gameLiveData

    private val _spaceStationListLiveData: MutableLiveData<List<SpaceStation>> = MutableLiveData()
    val spaceStationListLiveData: LiveData<List<SpaceStation>> = _spaceStationListLiveData

    private val _uiStateLiveData: MutableLiveData<UIState> = MutableLiveData()
    val uiStateLiveData: LiveData<UIState> = _uiStateLiveData

    private val _gameDone: MutableLiveData<GameResult> = MutableLiveData()
    val gameDone: LiveData<GameResult> = _gameDone

    private val _errorLiveData: MutableLiveData<ErrorModel> = MutableLiveData()
    val errorLiveData: LiveData<ErrorModel> = _errorLiveData

    private val _countDownTimer: MutableLiveData<Long> = MutableLiveData()
    val countDownTimer: LiveData<Long> = _countDownTimer

    private var isTimerRuning = false

    init {
        _uiStateLiveData.postValue(UIState.LOADING)
        collectFlowData()
        getStationInfo()
        getGameInfo()
    }

    fun processGameInfo(game: Game) {
        isGameDone(game)
        _gameLiveData.postValue(game)
        startTimer(game.durability.toLong())
    }

    fun processSpaceStationInfo(spaceStationList: List<SpaceStation>) {
        calculateDistanceFromCurrent(spaceStationList)
        gameLiveData.value?.let { isGameDone(it, spaceStationList) }
    }

    private fun collectFlowData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.flowSpaceStation.collect { spaceStationList ->
                when (spaceStationList.size) {
                    0 -> handleError(ErrorModel("Error"))
                    else -> {
                        processSpaceStationInfo(spaceStationList)
                    }
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.flowGame.collect { response ->
                when (response) {
                    is RepoResponse.Data -> processGameInfo(response.data)
                    is RepoResponse.Error -> handleError(response.error)
                }
            }
        }
    }

    fun getGameInfo() {
        viewModelScope.launch(Dispatchers.Main) {
            repository.getGameInfo()
        }
    }

    fun handleError(error: ErrorModel) {
        _errorLiveData.postValue(error)
    }

    fun getStationInfo() {
        viewModelScope.launch(Dispatchers.Main) {
            repository.getStationInfo()
        }
    }

    fun calculateDistanceFromCurrent(list: List<SpaceStation>) {

        val currentLocation = gameLiveData.value?.currentLocation
        val currentStation = list.find { currentLocation == it.name }
        var updatedList = list
        updatedList.forEach {
            var x1 = it.coordinateX
            var y1 = it.coordinateY
            var x2: Double = currentStation?.coordinateX
                ?: 0.0 // TODO()  First init'de dünyanın konumunu alıyor optimize edilebilir
            var y2: Double = currentStation?.coordinateY ?: 0.0
            val distance = sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2)).toInt()
            it.distanceFromCurrent = distance
        }
        _spaceStationListLiveData.postValue(updatedList)


    }

    fun changeFavStatus(item: SpaceStation) {
        item.isFavorite = item.isFavorite.not()
        updateStation(item)
    }

    fun updateStation(item: SpaceStation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStation(item)
        }
    }

    fun travel(
        currentGame: Game,
        spaceStation: SpaceStation
    ) {
        currentGame.currentLocation = spaceStation.name
        currentGame.storage -= spaceStation.need
        currentGame.speed -= spaceStation.distanceFromCurrent!!
        spaceStation.stock += spaceStation.need
        spaceStation.need = 0
        spaceStation.distanceFromCurrent = 0

        updateGame(currentGame)
        updateStation(spaceStation)


    }

    fun updateGame(gameItem: Game) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateGame(gameItem)
        }
    }

    private fun isGameDone(currentGame: Game, list: List<SpaceStation>? = null) {
        list?.let { list ->
            var totalNeed: Int = 0
            var minDistance: Int = Int.MAX_VALUE
            list.forEach {
                totalNeed += it.need
                if (it.need != 0 && it.distanceFromCurrent!! < minDistance) {
                    minDistance = it.distanceFromCurrent!!
                }
            }
            if (totalNeed == 0) {
                _gameDone.postValue(GameResult.WIN)
                return
            }
            if (totalNeed > currentGame.storage) {
                _gameDone.postValue(GameResult.LOSE)
                return
            }

            if (minDistance > currentGame.speed && minDistance != Int.MAX_VALUE) {
                _gameDone.postValue(GameResult.LOSE)
                return
            }
        }

        if (currentGame.damageCapacity == 0) {
            _gameDone.postValue(GameResult.LOSE)
        }


    }

    fun newGame() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.restartGame()
        }
    }

    fun checkState() {
        _uiStateLiveData.postValue(UIState.LIVE)
    }

    private fun startTimer(durability: Long) {
        if (!isTimerRuning) {
            viewModelScope.launch(Dispatchers.Main) {
                object : CountDownTimer(durability, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        _countDownTimer.postValue((millisUntilFinished / 1000) + 1)
                    }

                    override fun onFinish() {
                        takeDamage()
                        isTimerRuning = false

                    }
                }.start()
            }
        }
        isTimerRuning = true

    }

    fun takeDamage() {
        var game = gameLiveData.value

        game?.let {
            game.damageCapacity -= 10
            updateGame(game)
        }

    }


}


