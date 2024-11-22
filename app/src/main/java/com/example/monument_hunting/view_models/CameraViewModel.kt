package com.example.monument_hunting.view_models

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monument_hunting.repositories.MediaPipeRepository
import com.example.monument_hunting.utils.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val mediaPipeRepository: MediaPipeRepository
): ViewModel() {

    private var _processingImage = MutableStateFlow(Data.success<Boolean, String>(false))
    val processingImage = _processingImage.asStateFlow()

    fun classifyImage(uri: Uri?){
        _processingImage.value = Data.loading()
        viewModelScope.launch {
            if (uri != null) {
                try {
                    mediaPipeRepository.classifyImage(uri)
                    _processingImage.value = Data.success(true)
                } catch (e: Exception) {
                    _processingImage.value = Data.error(e.toString())
                    throw e
                }
            } else {
                _processingImage.value = Data.error("Could not retrieve image uri.")
            }
        }
    }

    fun resetState(){
        _processingImage.value = Data.success(false)
    }


}