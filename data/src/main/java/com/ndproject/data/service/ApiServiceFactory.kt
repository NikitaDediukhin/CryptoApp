package com.ndproject.data.service

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceFactory {
    fun createApiService(): ApiService {
        val gson = GsonBuilder().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiService::class.java)
    }
}