package com.example.monument_hunting.domain.api_domain


import com.google.gson.annotations.SerializedName

data class _Zone(
    @SerializedName("coordinates")
    var coordinates: List<_Coordinate> = listOf(),
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("region")
    var regionId: Int = 0
)