package com.ndproject.cryptoapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
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
import com.ndproject.cryptoapp.viewmodel.CryptoViewModel
import com.ndproject.cryptoapp.viewmodel.CryptoViewModelFactory

class MainActivity : AppCompatActivity() {

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
        viewModel.availableCurrenciesLiveData.observe(this) { currencyList ->
            setupCurrencyChips(currencyList)
        }
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

    private fun setupCurrencyChips(currencies: List<String>) {
        binding.chipGroup.removeAllViews() // Очистите все существующие чипсы перед добавлением новых

        val inflater = LayoutInflater.from(this@MainActivity)
        var selectedChip: Chip? = null

        currencies.forEach { currency ->
            // кастомный макет для чипса
            val chip = inflater.inflate(R.layout.custom_chip, binding.chipGroup, false) as Chip
            chip.text = currency
            chip.isCheckable = true
            chip.isCheckedIconVisible = false

            // Установка стиля для чипсов по умолчанию
            if(currency == viewModel.currentCurrencyLiveData.value){
                selectedChip = chip
            }

            // Обработка клика по чипсу
            chip.setOnClickListener {
                viewModel.changeCurrency(currency)
                // обновление состояния чипса
                updateChipState(chip)
            }

            binding.chipGroup.addView(chip)
        }
        // установить первое состояние для чипсов
        selectedChip?.let { updateChipState(it) }
    }



    private fun updateChipState(selectedChip: Chip) {
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            if (chip == selectedChip) {
                chip.setTextColor(ContextCompat.getColor(this, R.color.brightOrange))
                chip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.lightBeige)
            } else {
                chip.setTextColor(ContextCompat.getColor(this, R.color.blackTransparent87))
                chip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.grey)
            }
        }
    }

    // Настройка чипсов для выбора валюты
    private fun setupToolbar() {

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
}