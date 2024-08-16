package com.ndproject.data.repository

import com.ndproject.data.service.ApiService
import com.ndproject.domain.model.CryptoDetailsModel
import com.ndproject.domain.model.CryptoModel
import com.ndproject.domain.repository.CryptoRepository

class CryptoRepositoryImpl(
    private val cryptoMapper: CryptoMapper,
    private val apiService: ApiService
) : CryptoRepository {

    override suspend fun getCryptoMarket(vsCurrency: String): List<CryptoModel> {
        val response = apiService.getCryptoMarkets(vsCurrency)
        val model = response.map { cryptoMapper.toCryptoModel(it) }
        return model
    }

    override suspend fun getCryptoDetails(id: String): CryptoDetailsModel {
        val response = apiService.getCryptoDetails(id)
        val model = cryptoMapper.toCryptoDetailsModel(response)
        return model
    }

}