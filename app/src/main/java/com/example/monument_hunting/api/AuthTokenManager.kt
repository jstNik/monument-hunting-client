package com.example.monument_hunting.api

import android.content.Context
import com.example.monument_hunting.domain.AuthToken
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class AuthTokenManager(
    private val context: Context
) {

    val isRefreshTokenValid: Boolean
        get() {
            val token = extractToken()
            return token.refreshExpirationSec.seconds > System.currentTimeMillis().milliseconds
        }

    val isAccessTokenValid: Boolean
        get() {
            val token = extractToken()
            return token.accessExpirationSec.seconds > System.currentTimeMillis().milliseconds
        }

    fun extractToken(): AuthToken {
        val tk = context
            .getSharedPreferences("AuthToken", Context.MODE_PRIVATE)
            .getString("authToken", null)
        val authToken = Gson().fromJson(tk, object: TypeToken<AuthToken>(){ })
        return authToken
    }

    fun saveToken(authToken: AuthToken){
        context
            .getSharedPreferences("AuthToken", Context.MODE_PRIVATE)
            .edit()
            .putString("authToken", Gson().toJson(authToken))
            .apply()
    }

}