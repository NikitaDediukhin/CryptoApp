package com.ndproject.cryptoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ndproject.domain.usecase.ChangeCurrencyUseCase
import com.ndproject.domain.usecase.GetCryptoDetailsUseCase
import com.ndproject.domain.usecase.GetCryptoMarketUseCase

class CryptoViewModelFactory(
    private val getCryptoMarketUseCase: GetCryptoMarketUseCase,
    private val getCryptoDetailsUseCase: GetCryptoDetailsUseCase,
    private val changeCurrencyUseCase: ChangeCurrencyUseCase
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CryptoViewModel::class.java)) {
            return CryptoViewModel(
                getCryptoMarketUseCase = getCryptoMarketUseCase,
                getCryptoDetailsUseCase = getCryptoDetailsUseCase,
                changeCurrencyUseCase = changeCurrencyUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}