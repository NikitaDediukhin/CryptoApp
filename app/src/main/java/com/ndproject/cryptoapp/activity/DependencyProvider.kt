package com.ndproject.cryptoapp.activity

import com.ndproject.data.repository.CryptoMapper
import com.ndproject.data.repository.CryptoRepositoryImpl
import com.ndproject.data.service.ApiService
import com.ndproject.data.service.ApiServiceFactory
import com.ndproject.domain.repository.CryptoRepository
import com.ndproject.domain.usecase.ChangeCurrencyUseCase
import com.ndproject.domain.usecase.GetCryptoDetailsUseCase
import com.ndproject.domain.usecase.GetCryptoMarketUseCase

object DependencyProvider {
    private val apiService: ApiService by lazy { ApiServiceFactory.createApiService() }
    private val cryptoMapper: CryptoMapper by lazy { CryptoMapper }
    private val repository: CryptoRepository by lazy {
        CryptoRepositoryImpl(apiService = apiService, cryptoMapper = cryptoMapper)
    }
    val getCryptoMarketUseCase: GetCryptoMarketUseCase by lazy { GetCryptoMarketUseCase(repository) }
    val getCryptoDetailsUseCase: GetCryptoDetailsUseCase by lazy { GetCryptoDetailsUseCase(repository) }
    val changeCurrencyUseCase: ChangeCurrencyUseCase by lazy { ChangeCurrencyUseCase(repository) }
}