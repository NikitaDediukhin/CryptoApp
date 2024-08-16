package com.ndproject.domain.repository

import com.ndproject.domain.model.CryptoDetailsModel
import com.ndproject.domain.model.CryptoModel

interface CryptoRepository {

    suspend fun getCryptoMarket(vsCurrency: String): List<CryptoModel>

    suspend fun getCryptoDetails(id: String): CryptoDetailsModel
}