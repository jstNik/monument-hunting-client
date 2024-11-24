package com.example.monument_hunting.view_models

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monument_hunting.domain.Monument
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

    fun classifyImage(bitmap: Bitmap?, monument: Monument?){
        _processingImage.value = Data.loading()
        viewModelScope.launch {
            if (bitmap != null) {
                try {
                    val result = mediaPipeRepository.classifyImage(bitmap)
                    if(result == monument?.category)
                        _processingImage.value = Data.success(true)
                    else
                        _processingImage.value = Data.error("Sorry, i haven't recognized the monument. Try again!")
                } catch (e: Exception) {
                    _processingImage.value = Data.error(e.toString())
                }
            } else {
                _processingImage.value = Data.error("Could not obtain image.")
            }
        }
    }

    fun resetState(){
        _processingImage.value = Data.success(false)
    }


}