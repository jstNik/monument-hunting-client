package com.example.monument_hunting.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monument_hunting.domain.AuthResponse
import com.example.monument_hunting.domain.Player
import com.example.monument_hunting.exceptions.ApiRequestException
import com.example.monument_hunting.repositories.ApiRepository
import com.example.monument_hunting.utils.Data
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginSignupViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _loginSignupSuc = MutableStateFlow(Data.success<Player, String>(null))
    val loginSignupSuc
        get() = _loginSignupSuc.asStateFlow()

    fun verifyToken() {
        viewModelScope.launch() {
            try {
                val res = repository.verifyToken()
                _loginSignupSuc.value = Data.success(res)
            } catch (e: ApiRequestException) {
                Log.d("${e.responseCode}", "${e.message}")
            }
        }
    }

    fun loginSignup(signup: Boolean, username: String, email: String, password: String) {

        _loginSignupSuc.value = Data.loading()
        viewModelScope.launch() {
            try {
                val player = repository.loginSignup(signup, username, email, password)
                _loginSignupSuc.value = Data.success(player)
            } catch (ex: ApiRequestException) {
                var error = "Error"
                try{
                    val json = Gson().fromJson(ex.message, JsonObject::class.java)
                    error = json.get("error").asString
                } catch(_: Exception) {
                    // Do Nothing
                }
                _loginSignupSuc.value = Data.error(error)
            }
        }
    }


}