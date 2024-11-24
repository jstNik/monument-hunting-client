package com.example.monument_hunting.domain.api_domain


import com.google.gson.annotations.SerializedName

data class _Riddle(
    @SerializedName("body")
    var body: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("monument")
    var monument: _Monument = _Monument(),
    @SerializedName("name")
    var name: String = ""
)