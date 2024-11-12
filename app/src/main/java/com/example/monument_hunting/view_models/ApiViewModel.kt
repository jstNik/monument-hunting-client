package com.example.monument_hunting.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monument_hunting.domain.Zone
import com.example.monument_hunting.exceptions.ApiRequestFailed
import com.example.monument_hunting.repositories.ApiRepository
import com.example.monument_hunting.utils.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiViewModel @Inject constructor(
    val repository: ApiRepository
) : ViewModel() {

    private val _zones = MutableStateFlow(Data.loading<List<Zone>, ApiRequestFailed>())
    val zones get() = _zones.asStateFlow()

    fun requestZones(){
        viewModelScope.launch {
            try {
                val zoneList = repository.requestAllZones()
                _zones.value = Data.success<List<Zone>, ApiRequestFailed>(zoneList)
            } catch (ex: ApiRequestFailed) {
                _zones.value = Data.error<List<Zone>, ApiRequestFailed>(ex)
            }
        }
    }

}