package com.ndproject.cryptoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndproject.domain.model.CryptoDetailsModel
import com.ndproject.domain.model.CryptoModel
import com.ndproject.domain.usecase.ChangeCurrencyUseCase
import com.ndproject.domain.usecase.GetCryptoDetailsUseCase
import com.ndproject.domain.usecase.GetCryptoMarketUseCase
import com.ndproject.domain.utils.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CryptoViewModel(
    private val getCryptoMarketUseCase: GetCryptoMarketUseCase,
    private val getCryptoDetailsUseCase: GetCryptoDetailsUseCase,
    private val changeCurrencyUseCase: ChangeCurrencyUseCase
) : ViewModel() {

    private val _cryptoListLiveData = MutableLiveData<DataState<List<CryptoModel>>>()
    val cryptoListLiveData: LiveData<DataState<List<CryptoModel>>> get() = _cryptoListLiveData

    private val _cryptoDetailsLiveData = MutableLiveData<DataState<CryptoDetailsModel>>()
    val cryptoDetailsLiveData: LiveData<DataState<CryptoDetailsModel>> get() = _cryptoDetailsLiveData

    private val _currentCurrency = MutableLiveData("usd")
    val currentCurrency: LiveData<String> get() = _currentCurrency

    fun fetchCryptoMarket(vsCurrency: String) {
        viewModelScope.launch {
            _cryptoListLiveData.value = DataState.Loading
            delay(1000)
            _cryptoListLiveData.value = getCryptoMarketUseCase.execute(vsCurrency)
        }
    }

    fun changeCurrency(vsCurrency: String) {
        _currentCurrency.value = vsCurrency
        viewModelScope.launch {
            _cryptoListLiveData.value = DataState.Loading
            delay(1000)
            _cryptoListLiveData.value = changeCurrencyUseCase.execute(vsCurrency)
        }
    }

    fun fetchCryptoDetails(id: String) {
        viewModelScope.launch {
            _cryptoDetailsLiveData.value = DataState.Loading
            delay(1000)
            _cryptoDetailsLiveData.value = getCryptoDetailsUseCase.execute(id)
        }
    }
}
