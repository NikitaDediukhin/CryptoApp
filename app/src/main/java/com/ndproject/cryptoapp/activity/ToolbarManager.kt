package com.ndproject.cryptoapp.activity

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.material.chip.Chip
import com.ndproject.cryptoapp.R
import com.ndproject.cryptoapp.databinding.ActivityMainBinding
import com.ndproject.cryptoapp.viewmodel.CryptoViewModel

class ToolbarManager(
    private val binding: ActivityMainBinding,
    private val viewModel: CryptoViewModel,
    private val navController: NavController
) {

    fun setupToolbar() {
        // Настройка NavController для обработки смены тулбара
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when (destination.id) {
                R.id.cryptoListFragment -> {
                    showMainToolbar()
                }
                R.id.cryptoDetailsFragment -> {
                    showDetailsToolbar(arguments?.getString("cryptoName") ?: "")
                }
            }
        }

        // Обработка нажатия кнопки назад
        binding.ivBackButton.setOnClickListener {
            navController.navigateUp()
        }
    }

    // Настройка Toolbar для списка
    private fun showMainToolbar() {
        binding.toolbar.findViewById<LinearLayout>(R.id.mainTools).visibility = View.VISIBLE
        binding.toolbar.findViewById<LinearLayout>(R.id.toolbarDescription).visibility = View.GONE
    }

    // Настройка Toolbar для описания
    private fun showDetailsToolbar(cryptoName: String) {
        binding.toolbar.findViewById<LinearLayout>(R.id.mainTools).visibility = View.GONE
        binding.toolbar.findViewById<LinearLayout>(R.id.toolbarDescription).visibility = View.VISIBLE
        // Установка текста в TextView с названием криптовалюты
        binding.toolbar.findViewById<TextView>(R.id.tvToolbarDescriptionTitle).text = cryptoName
    }

    // Настройка чипсов для выбора валюты
    fun setupCurrencyChips(currencies: List<String>) {
        // Очистить все существующие чипсы перед добавлением новых
        binding.chipGroup.removeAllViews()

        val inflater = LayoutInflater.from(binding.root.context)
        var selectedChip: Chip? = null

        currencies.forEach { currency ->
            // Инфлейт кастомный макет для чипса
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
                // Обновление состояния чипса
                updateChipState(chip)
            }

            binding.chipGroup.addView(chip)
        }
        // Установить первое состояние для чипсов
        selectedChip?.let { updateChipState(it) }
    }

    private fun updateChipState(selectedChip: Chip) {
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            if (chip == selectedChip) {
                chip.setTextColor(ContextCompat.getColor(binding.root.context, R.color.brightOrange))
                chip.chipBackgroundColor = ContextCompat.getColorStateList(binding.root.context, R.color.lightBeige)
            } else {
                chip.setTextColor(ContextCompat.getColor(binding.root.context, R.color.blackTransparent87))
                chip.chipBackgroundColor = ContextCompat.getColorStateList(binding.root.context, R.color.grey)
            }
        }
    }
}