package com.ndproject.domain.model

data class CryptoModel(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val currentPrice: Double,
    val priceChange: Double
)