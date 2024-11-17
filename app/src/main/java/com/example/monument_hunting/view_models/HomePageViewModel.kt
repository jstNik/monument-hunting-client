package com.example.monument_hunting.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monument_hunting.domain.ServerData
import com.example.monument_hunting.exceptions.ApiRequestException
import com.example.monument_hunting.repositories.ApiRepository
import com.example.monument_hunting.utils.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    val repository: ApiRepository
) : ViewModel() {

    private val _serverData = MutableStateFlow(Data.loading<ServerData, ApiRequestException>())
    val serverData get() = _serverData.asStateFlow()

    fun requestZones() {
        try {
            _serverData.value = Data.loading<ServerData, ApiRequestException>()
            viewModelScope.launch {
                val zoneList = repository.requestAllZones()
                _serverData.value = Data.success<ServerData, ApiRequestException>(zoneList)
            }
        } catch (ex: ApiRequestException) {
            _serverData.value = Data.error<ServerData, ApiRequestException>(ex)
        }
    }

}