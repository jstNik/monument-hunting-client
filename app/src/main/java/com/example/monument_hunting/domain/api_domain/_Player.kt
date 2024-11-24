package com.example.monument_hunting.domain.api_domain


import com.google.gson.annotations.SerializedName

data class _Player(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("username")
    var username: String = ""
)