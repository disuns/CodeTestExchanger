package com.example.codetestexchanger.dataclass

data class RestData(
    val quotes: Quotes,
    val source: String,
    val success: Boolean,
    val timestamp: Int
)