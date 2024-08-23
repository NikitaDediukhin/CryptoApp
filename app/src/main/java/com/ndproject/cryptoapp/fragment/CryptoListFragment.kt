package com.ndproject.cryptoapp.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

        // Начальная загрузка данных о криптовалютах, если это не было сделано ранее
        if (!viewModel.isListDataLoaded) {
            viewModel.currentCurrencyLiveData.value?.let {
                viewModel.fetchCryptoMarket(it)
            }
            viewModel.isListDataLoaded = true
        }
        // Сброс флага загрузки данных о деталях криптовалют
        viewModel.isDetailsDataLoaded = false
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
            when (state) {
                is DataState.Loading -> {
                    showLoading(true)
                    binding.recyclerView.visibility = View.GONE
                    binding.marketErrorLayout.visibility = View.GONE
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

    // Передача состояния загрузки в активити
    private fun showLoading(isLoading: Boolean) {
        (requireActivity() as MainActivity).showLoading(isLoading)
    }

    // Отображение загруженных данных
    private fun showCryptoList(data: List<CryptoModel>) {
        showLoading(false)
        binding.marketErrorLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE

        adapter.setListData(data, viewModel.currentCurrencyLiveData.value?.let {
            if (it == "usd") "$" else "₽"
        } ?: "$")
    }

    // Отображение сообщения об ошибке и предоставление возможности повторной загрузки
    private fun showError() {
        showLoading(false)
        binding.recyclerView.visibility = View.GONE
        binding.marketErrorLayout.visibility = View.VISIBLE
        binding.btnMarketRetry.setOnClickListener {
            Log.e("crypto", "List trying")
            viewModel.currentCurrencyLiveData.value?.let { viewModel.fetchCryptoMarket(it) }
        }
    }

    private fun navigateToDetails(cryptoId: String, cryptoTitle: String) {
        // Загрузка данных о криптавалюте по id
        val action = CryptoListFragmentDirections.actionCryptoListFragmentToCryptoDetailsFragment(cryptoId, cryptoTitle)
        findNavController().navigate(action)
    }
}

