package com.ndproject.cryptoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndproject.cryptoapp.activity.DependencyProvider.changeCurrencyUseCase
import com.ndproject.domain.model.CryptoDetailsModel
import com.ndproject.domain.model.CryptoModel
import com.ndproject.domain.usecase.ChangeCurrencyUseCase
import com.ndproject.domain.usecase.GetCryptoDetailsUseCase
import com.ndproject.domain.usecase.GetCryptoMarketUseCase
import com.ndproject.domain.utils.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel для управления состоянием данных криптовалют и обработки бизнес-логики.
 * Интеракция с UseCases для получения данных и изменения валюты.
 */
class CryptoViewModel(
    private val getCryptoMarketUseCase: GetCryptoMarketUseCase,
    private val getCryptoDetailsUseCase: GetCryptoDetailsUseCase
) : ViewModel() {

    // LiveData для списка криптовалют
    private val _cryptoListLiveData = MutableLiveData<DataState<List<CryptoModel>>>()
    val cryptoListLiveData: LiveData<DataState<List<CryptoModel>>> get() = _cryptoListLiveData

    // LiveData для подробной информации о криптовалюте
    private val _cryptoDetailsLiveData = MutableLiveData<DataState<CryptoDetailsModel>>()
    val cryptoDetailsLiveData: LiveData<DataState<CryptoDetailsModel>> get() = _cryptoDetailsLiveData

    // LiveData для текущей валюты
    private val _currentCurrency = MutableLiveData("usd")
    val currentCurrency: LiveData<String> get() = _currentCurrency

    // Флаги для отслеживания состояния загрузки данных
    var isListDataLoaded = false
    var isDetailsDataLoaded = false

    /**
     * Запрашивает данные о криптовалютах для указанной валюты.
     * Обновляет LiveData с состоянием загрузки и результатами.
     *
     * @param vsCurrency Валюта, по отношению к которой нужно получить данные о криптовалютах.
     */
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
