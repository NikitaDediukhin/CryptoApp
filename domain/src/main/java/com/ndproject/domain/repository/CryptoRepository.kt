package com.ndproject.domain.repository

import com.ndproject.domain.model.CryptoDetailsModel
import com.ndproject.domain.model.CryptoModel
import com.ndproject.domain.utils.DataState

interface CryptoRepository {

    suspend fun getCryptoMarket(vsCurrency: String): DataState<List<CryptoModel>>

    suspend fun getCryptoDetails(id: String): DataState<CryptoDetailsModel>
}