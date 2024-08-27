package com.ndproject.cryptoapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.chip.Chip
import com.ndproject.cryptoapp.R
import com.ndproject.cryptoapp.databinding.ActivityMainBinding
import com.ndproject.cryptoapp.viewmodel.CryptoViewModel
import com.ndproject.cryptoapp.viewmodel.CryptoViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // Настройка ViewModel
    private val viewModel: CryptoViewModel by lazy {
        ViewModelProvider(
            this,
            CryptoViewModelFactory(
                getCryptoMarketUseCase = DependencyProvider.getCryptoMarketUseCase,
                getCryptoDetailsUseCase = DependencyProvider.getCryptoDetailsUseCase,
                changeCurrencyUseCase = DependencyProvider.changeCurrencyUseCase
            )
        )[CryptoViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Настройка toolbar
        val toolbarManager = ToolbarManager(binding, viewModel, navController)
        toolbarManager.setupToolbar()
        // Обработка изменения валюты
        viewModel.availableCurrenciesLiveData.observe(this) { currencyList ->
            toolbarManager.setupCurrencyChips(currencyList)
        }
    }

    fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}