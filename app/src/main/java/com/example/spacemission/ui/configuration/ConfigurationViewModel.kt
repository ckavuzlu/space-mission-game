package com.example.spacemission.ui.configuration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacemission.R
import com.example.spacemission.database.entities.Game
import com.example.spacemission.model.*
import com.example.spacemission.repository.GameRepository
import com.example.spacemission.util.Constants
import com.example.spacemission.util.Constants.DURABILITY_MULTIPLY
import com.example.spacemission.util.Constants.INITIAL_REMAINING_POINTS
import com.example.spacemission.util.Constants.INITIAL_SKILL_POINT
import com.example.spacemission.util.Constants.SPEED_MULTIPLY
import com.example.spacemission.util.Constants.STORAGE_MULTIPLY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(private val repository: GameRepository) :
    ViewModel() {

    private val _activeGame = MutableLiveData<Game>()
    val activeGame: LiveData<Game> = _activeGame

    private val _errorMessageLiveData = MutableLiveData<Int>()
    val errorMessageLiveData: LiveData<Int> = _errorMessageLiveData

    val durabilityLiveData: MutableLiveData<Float> = MutableLiveData(INITIAL_SKILL_POINT)
    val storageLiveData: MutableLiveData<Float> = MutableLiveData(INITIAL_SKILL_POINT)
    val speedLiveData: MutableLiveData<Float> = MutableLiveData(INITIAL_SKILL_POINT)
    val remainingSkillLiveData: MutableLiveData<Int> = MutableLiveData(INITIAL_REMAINING_POINTS)
    val spaceshipNameLiveData: MutableLiveData<String> = MutableLiveData()


    init {
        checkIsThereGame()
        viewModelScope.launch {
            repository.flowGame.collect { response ->
                when (response) {
                    is RepoResponse.Data -> _activeGame.postValue(response.data)
                }
            }
        }
    }

    fun checkIsThereGame() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getGameInfo()
        }
    }

    fun checkRemainingSkill(): Int {
        val newRemainingSkill =
            INITIAL_REMAINING_POINTS - durabilityLiveData.value!! - storageLiveData.value!! - speedLiveData.value!!
        return newRemainingSkill.toInt()
    }

    fun checkCredentials(): Boolean {
        if (spaceshipNameLiveData.value.isNullOrEmpty()) {
            _errorMessageLiveData.postValue(R.string.error_empty_name)
            return false
        }
        if (remainingSkillLiveData.value!! > 0) {
            _errorMessageLiveData.postValue(R.string.error_left_points)
            return false
        } else if (remainingSkillLiveData.value!! < 0) {
            _errorMessageLiveData.postValue(R.string.error_out_of_points)
            return false
        }
        if (durabilityLiveData.value == 0.0F || storageLiveData.value == 0.0F || speedLiveData.value == 0.0F) {
            _errorMessageLiveData.postValue(R.string.error_zero_skill)
            return false
        }
        insertGame(
            Game(
                durability = durabilityLiveData.value!!.times(DURABILITY_MULTIPLY),
                speed = speedLiveData.value!!.times(SPEED_MULTIPLY),
                storage = storageLiveData.value!!.times(STORAGE_MULTIPLY),
                spaceshipName = spaceshipNameLiveData.value!!,
                currentLocation = Constants.INITIAL_CURRENT_LOCATION
            )
        )
        return true
    }

    fun insertGame(game: Game) {
        viewModelScope.launch(Dispatchers.Main) {
            repository.insertGame(game)
        }
    }

}