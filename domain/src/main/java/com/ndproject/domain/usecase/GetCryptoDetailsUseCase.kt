package com.ndproject.domain.usecase

import com.ndproject.domain.model.CryptoDetailsModel
import com.ndproject.domain.repository.CryptoRepository
import com.ndproject.domain.utils.DataState

class GetCryptoDetailsUseCase(private val repository: CryptoRepository) {
    suspend fun execute(id: String): DataState<CryptoDetailsModel> {
        return repository.getCryptoDetails(id = id)
    }
}