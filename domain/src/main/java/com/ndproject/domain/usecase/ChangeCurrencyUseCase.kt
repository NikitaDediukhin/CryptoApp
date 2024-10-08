package com.ndproject.domain.usecase

import com.ndproject.domain.model.CryptoModel
import com.ndproject.domain.repository.CryptoRepository
import com.ndproject.domain.utils.DataState

class ChangeCurrencyUseCase(private val repository: CryptoRepository) {
    suspend fun execute(vsCurrency: String): DataState<List<CryptoModel>> {
        return repository.getCryptoMarket(vsCurrency = vsCurrency)
    }
}