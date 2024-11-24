package com.example.monument_hunting.domain

data class Riddle(

    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    var isFound: Boolean = false

)