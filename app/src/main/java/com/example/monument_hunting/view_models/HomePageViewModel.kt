package com.example.monument_hunting.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monument_hunting.domain.Catalog
import com.example.monument_hunting.domain.Riddle
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
    private val repository: ApiRepository
) : ViewModel() {

    private val _catalog = MutableStateFlow(Data.loading<Catalog, ApiRequestException>())
    val catalog get() = _catalog.asStateFlow()

    fun postRiddle(riddle: Riddle){
        _catalog.value = Data.loading()
        viewModelScope.launch {
            try {
                riddle.isFound = repository.postRiddle(riddle.id)
                _catalog.value = Data.success(_catalog.value.data)
            } catch (ex: ApiRequestException) {
                _catalog.value = Data.error(ex)
            }
        }
    }

    fun requestData() {
        _catalog.value = Data.loading()
        viewModelScope.launch {
            try {
                val data = repository.requestData()
                _catalog.value = Data.success(data.toCatalog())
            } catch (ex: ApiRequestException) {
                _catalog.value = Data.error(ex)
            }
        }
    }

}