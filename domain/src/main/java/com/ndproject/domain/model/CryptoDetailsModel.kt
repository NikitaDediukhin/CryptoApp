package com.ndproject.domain.model

data class CryptoDetailsModel(
    val id: String,
    val symbol: String,
    val name: String,
    val description: String,
    val categories: List<String>,
    val image: String
)