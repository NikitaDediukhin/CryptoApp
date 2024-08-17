package com.ndproject.cryptoapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.chip.Chip
import com.ndproject.cryptoapp.R
import com.ndproject.cryptoapp.databinding.ActivityMainBinding
import com.ndproject.cryptoapp.fragment.CryptoListFragment
import com.ndproject.cryptoapp.fragment.ErrorFragment
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
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Настойка ViewModel
        setupViewModel()

        // Настройка Toolbar
        setupToolbar()

        // Обработка изменения валюты
        updateCurrencyChips()
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment)
        if (navController.currentBackStackEntry != null) {
            navController.popBackStack()
        } else {
            super.onBackPressed()
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

    private fun updateCurrencyChips() {
        if (viewModel.currentCurrency.value == "usd") {
            updateChipState(binding.chipUsd, binding.chipRub)
        } else {
            updateChipState(binding.chipRub, binding.chipUsd)
        }
    }

    private fun setupToolbar() {
        // Настройка чипсов для выбора валюты
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
                    binding.toolbar.findViewById<LinearLayout>(R.id.main_tools).visibility = View.VISIBLE
                    binding.toolbar.findViewById<LinearLayout>(R.id.toolbar_description).visibility = View.GONE
                }
                R.id.cryptoDetailsFragment -> {
                    // Настройка Toolbar для описания
                    binding.toolbar.findViewById<LinearLayout>(R.id.main_tools).visibility = View.GONE
                    binding.toolbar.findViewById<LinearLayout>(R.id.toolbar_description).visibility = View.VISIBLE

                    // Установка текста в TextView с названием криптовалюты
                    val cryptoName = arguments?.getString("cryptoName") ?: ""
                    binding.toolbar.findViewById<TextView>(R.id.toolbar_description_title).text = cryptoName
                }
            }
        }

        // Обработка нажатия кнопки назад
        binding.toolbar.findViewById<ImageButton>(R.id.desc_tools).setOnClickListener {
            onBackPressed()
        }
    }



    private fun updateChipState(activeChip: Chip, inactiveChip: Chip) {
        activeChip.setTextColor(ContextCompat.getColor(this, R.color.brightOrange))
        activeChip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.lightBeige)

        inactiveChip.setTextColor(ContextCompat.getColor(this, R.color.blackTransparent))
        inactiveChip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.lightGrey)
    }
}