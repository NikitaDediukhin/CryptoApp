package com.ndproject.data.repository

import com.ndproject.data.model.CryptoDetailsResponse
import com.ndproject.data.model.CryptoResponse
import com.ndproject.domain.model.CryptoDetailsModel
import com.ndproject.domain.model.CryptoModel

object CryptoMapper {

    fun toCryptoModel(cryptoResponse: CryptoResponse): CryptoModel {
        return CryptoModel(
            id = cryptoResponse.id,
            symbol = cryptoResponse.symbol,
            name = cryptoResponse.name,
            image = cryptoResponse.image,
            currentPrice = cryptoResponse.currentPrice,
            priceChange = cryptoResponse.priceChangePercentage24h
        )
    }

    fun toCryptoDetailsModel(cryptoDetailsResponse: CryptoDetailsResponse): CryptoDetailsModel {
        return CryptoDetailsModel(
            id = cryptoDetailsResponse.id,
            symbol = cryptoDetailsResponse.symbol,
            name = cryptoDetailsResponse.name,
            description = cryptoDetailsResponse.description.ru,
            categories = cryptoDetailsResponse.categories,
            image = cryptoDetailsResponse.image.large
        )
    }
}