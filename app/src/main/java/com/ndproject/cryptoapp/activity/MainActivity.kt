package com.ndproject.cryptoapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.chip.Chip
import com.ndproject.cryptoapp.R
import com.ndproject.cryptoapp.databinding.ActivityMainBinding
import com.ndproject.cryptoapp.fragment.CryptoListFragment
import com.ndproject.cryptoapp.fragment.OnCurrencyChangedListener
import com.ndproject.cryptoapp.viewmodel.CryptoViewModel
import com.ndproject.cryptoapp.viewmodel.CryptoViewModelFactory

class MainActivity : AppCompatActivity(), OnCurrencyChangedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModelFactory: CryptoViewModelFactory
    lateinit var viewModel: CryptoViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // Настойка ViewModel
        setupViewModel()

        // Настройка Toolbar
        setupToolbar()

        // Обработка изменения валюты
        updateCurrencyChips()
    }

    fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupViewModel() {
        viewModelFactory = CryptoViewModelFactory(
            getCryptoMarketUseCase = DependencyProvider.getCryptoMarketUseCase,
            getCryptoDetailsUseCase = DependencyProvider.getCryptoDetailsUseCase,
            changeCurrencyUseCase = DependencyProvider.changeCurrencyUseCase
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(CryptoViewModel::class.java)
    }

    private fun updateCurrencyChips() {
        if (viewModel.currentCurrency.value == "usd") {
            updateChipState(binding.chipUsd, binding.chipRub)
        } else {
            updateChipState(binding.chipRub, binding.chipUsd)
        }
    }

    // Настройка чипсов для выбора валюты
    private fun setupToolbar() {
        binding.chipUsd.setOnClickListener {
            viewModel.changeCurrency("usd")
            updateChipState(
                activeChip = binding.chipUsd,
                inactiveChip = binding.chipRub
            )
        }
        binding.chipRub.setOnClickListener {
            viewModel.changeCurrency("rub")
            updateChipState(
                activeChip = binding.chipRub,
                inactiveChip = binding.chipUsd
            )
        }

        // Настройка NavController для обработки смены тулбара
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when (destination.id) {
                R.id.cryptoListFragment -> {
                    // Настройка Toolbar для списка
                    binding.toolbar.findViewById<LinearLayout>(R.id.mainTools).visibility = View.VISIBLE
                    binding.toolbar.findViewById<LinearLayout>(R.id.toolbarDescription).visibility = View.GONE
                }
                R.id.cryptoDetailsFragment -> {
                    // Настройка Toolbar для описания
                    binding.toolbar.findViewById<LinearLayout>(R.id.mainTools).visibility = View.GONE
                    binding.toolbar.findViewById<LinearLayout>(R.id.toolbarDescription).visibility = View.VISIBLE

                    // Установка текста в TextView с названием криптовалюты
                    val cryptoName = arguments?.getString("cryptoName") ?: ""
                    binding.toolbar.findViewById<TextView>(R.id.tvToolbarDescriptionTitle).text = cryptoName
                }
            }
        }

        // Обработка нажатия кнопки назад
        binding.ivBackButton.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun updateChipState(activeChip: Chip, inactiveChip: Chip) {
        activeChip.setTextColor(ContextCompat.getColor(this, R.color.brightOrange))
        activeChip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.lightBeige)

        inactiveChip.setTextColor(ContextCompat.getColor(this, R.color.blackTransparent87))
        inactiveChip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.grey)
    }

    override fun onCurrencyChanged(currency: String) {
        val fragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)?.childFragmentManager?.fragments?.find { it is CryptoListFragment }
        if (fragment is CryptoListFragment) {
            fragment.viewModel.fetchCryptoMarket(currency)
        }
    }
}