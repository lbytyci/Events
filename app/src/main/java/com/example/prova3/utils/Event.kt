package com.example.prova3.utils

data class Event(
    val id: String = "",
    val name: String = "",
    val date: String = "",
    val time: String = "",
    val place: String = "",
    val description: String = "",
    var ticketsAvailable: Int = 0,
    var ticketsPrice: String = "",
    var userInterested: Boolean = false
)
