package com.example.spacemission.ui.favorite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacemission.database.entities.SpaceStation
import com.example.spacemission.model.ErrorModel
import com.example.spacemission.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor(private val repository: GameRepository): ViewModel() {

    private val _favoriteListLiveData : MutableLiveData<List<SpaceStation>> = MutableLiveData()
    val favoriteListLiveData : MutableLiveData<List<SpaceStation>> = _favoriteListLiveData

    init {
        collectFlowData()
        getFavoriteList()
    }

    fun collectFlowData(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.flowFavSpaceStation.collect { favStationList ->
                _favoriteListLiveData.postValue(favStationList)
            }
        }
    }
    fun getFavoriteList(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFavList()
        }
    }
    fun updateStation(item: SpaceStation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStation(item)
        }
    }

}