package com.ndproject.cryptoapp.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ndproject.cryptoapp.R
import com.ndproject.cryptoapp.activity.MainActivity
import com.ndproject.cryptoapp.databinding.FragmentCryptoListBinding
import com.ndproject.cryptoapp.fragment.adapter.CryptoAdapter
import com.ndproject.cryptoapp.viewmodel.CryptoViewModel
import com.ndproject.domain.model.CryptoModel
import com.ndproject.domain.utils.DataState

/**
 * Fragment для отображения списка криптовалют.
 * Обрабатывает загрузку данных, отображение ошибок и обновление UI.
 */
class CryptoListFragment : Fragment() {

    private lateinit var binding: FragmentCryptoListBinding
    private lateinit var adapter: CryptoAdapter

    val viewModel: CryptoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCryptoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        setupObservers()

        setupSwipeToRefresh()

        // Начальная загрузка данных о криптовалютах,
        // если это не было сделано ранее
        if (!viewModel.isListDataLoaded) {
            viewModel.currentCurrencyLiveData.value?.let {
                viewModel.fetchCryptoMarket(it)
            }
            viewModel.isListDataLoaded = true
        }
        // Сброс флага загрузки данных о деталях криптовалют
        viewModel.isDetailsDataLoaded = false
    }

    // Настройка Swipe-to-Refresh
    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchCryptoMarket(viewModel.currentCurrencyLiveData.value.orEmpty(), isRefresh = true)
        }
    }

    // Инициализация адаптера для RecyclerView
    private fun setupRecyclerView() {
        adapter = CryptoAdapter { cryptoId, name ->
            navigateToDetails(cryptoId, name)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.cryptoListLiveData.observe(viewLifecycleOwner) { state ->
            // Прекращение анимации обновления при изменении состояния
            if (viewModel.isRefreshing.value == true) {
                binding.swipeRefreshLayout.isRefreshing = false
            }

            when (state) {
                is DataState.Loading -> {
                    // индикатор загрузки, если данные не обновляются через Swipe-to-Refresh
                    if (viewModel.isRefreshing.value == false) {
                        showLoading(true)
                    }
                }
                is DataState.Success -> {
                    if (state.data.isEmpty()) {
                        showError()
                    } else {
                        showCryptoList(state.data)
                    }
                }
                is DataState.Error -> {
                    showError()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.recyclerView.visibility = View.GONE
            binding.marketErrorLayout.visibility = View.GONE
            (requireActivity() as MainActivity).showLoading(true)
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.marketErrorLayout.visibility = View.GONE
            (requireActivity() as MainActivity).showLoading(false)
        }
    }

    private fun showCryptoList(data: List<CryptoModel>) {
        showLoading(false)
        adapter.setListData(data, viewModel.currentCurrencyLiveData.value?.let {
            if (it == "usd") "$" else "₽"
        } ?: "$")
    }

    //Отображение ошибки. В зависимости
    //от состояния обновления отображается Snackbar или Layout.
    private fun showError() {
        if (viewModel.isRefreshing.value == true) {
            showSnackbarError()
        } else {
            showErrorLayout()
        }
    }

    private fun showErrorLayout() {
        Log.e("crypto", "error layout")
        showLoading(false)
        binding.recyclerView.visibility = View.GONE
        binding.marketErrorLayout.visibility = View.VISIBLE
        binding.btnMarketRetry.setOnClickListener {
            Log.e("crypto", "List trying")
            viewModel.currentCurrencyLiveData.value?.let { viewModel.fetchCryptoMarket(it) }
        }
    }

    @SuppressLint("RestrictedApi", "InflateParams")
    private fun showSnackbarError() {
        // Остановка анимации обновления
        binding.swipeRefreshLayout.isRefreshing = false

        val snackbar = Snackbar.make(binding.root, R.string.error_text_snackbar, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view as Snackbar.SnackbarLayout
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)
        snackbarView.removeAllViews()

        val customView = layoutInflater.inflate(R.layout.custom_snackbar, null)
        val textView = customView.findViewById<TextView>(R.id.snackbar_text)
        textView.text = getString(R.string.error_text_snackbar)

        snackbarView.addView(customView, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        snackbar.show()
    }

    private fun navigateToDetails(cryptoId: String, cryptoTitle: String) {
        val action = CryptoListFragmentDirections.actionCryptoListFragmentToCryptoDetailsFragment(cryptoId, cryptoTitle)
        findNavController().navigate(action)
    }
}

