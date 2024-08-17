package com.ndproject.data.repository

import android.util.Log
import com.google.gson.internal.GsonBuildConfig
import com.ndproject.data.BuildConfig
import com.ndproject.data.service.ApiService
import com.ndproject.domain.model.CryptoDetailsModel
import com.ndproject.domain.model.CryptoModel
import com.ndproject.domain.repository.CryptoRepository
import com.ndproject.domain.utils.DataState

class CryptoRepositoryImpl(
    private val cryptoMapper: CryptoMapper,
    private val apiService: ApiService
) : CryptoRepository {

    private val key = BuildConfig.api_key

    override suspend fun getCryptoMarket(vsCurrency: String): DataState<List<CryptoModel>> {
        return try {
            val response = apiService.getCryptoMarkets(vsCurrency = vsCurrency, apiKey = key)
            val model = response.body()?.map { cryptoMapper.toCryptoModel(it) }
            if (model.isNullOrEmpty()) {
                DataState.Error("No data received or response body is null")
            } else {
                DataState.Success(model)
            }
        } catch (e: Exception) {
            DataState.Error(e.message.toString())
        }
    }

    override suspend fun getCryptoDetails(id: String):  DataState<CryptoDetailsModel> {
        return try {
            val response = apiService.getCryptoDetails(id = id, apiKey = key)
            val model = cryptoMapper.toCryptoDetailsModel(response.body() ?: throw Exception("Response body is null"))
            DataState.Success(model)
        } catch (e: Exception) {
            DataState.Error(e.message.toString())
        }
    }

}