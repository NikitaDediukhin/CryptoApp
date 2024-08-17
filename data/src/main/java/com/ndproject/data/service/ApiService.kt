package com.ndproject.data.service

import com.ndproject.data.model.CryptoDetailsResponse
import com.ndproject.data.model.CryptoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

const val NUM_PER_PAGE = 30
const val NUM_PAGE = 1
const val LANGUAGE = "en"

interface ApiService {
    @GET("coins/markets")
    suspend fun getCryptoMarkets(
        @Query("vs_currency") vsCurrency: String,
        @Query("per_page") perPage: Int = NUM_PER_PAGE,
        @Query("page") page: Int = NUM_PAGE,
        @Query("locale") locale: String = LANGUAGE,
        @Query("api_key") apiKey: String
    ): Response<List<CryptoResponse>>

    @GET("coins/{id}")
    suspend fun getCryptoDetails(
        @Path("id") id: String,
        @Query("localization") localization: String = LANGUAGE,
        @Query("api_key") apiKey: String
    ): Response<CryptoDetailsResponse>
}