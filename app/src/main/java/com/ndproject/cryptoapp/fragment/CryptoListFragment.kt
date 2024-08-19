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
    private var currencyChangedListener: OnCurrencyChangedListener? = null
    private var hasLoadedData = false

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

        // Настройка RecyclerView
        setupRecyclerView()

        // Установка наблюдателей для LiveData
        setupObservers()

        // Начальная загрузка данных о криптовалютах, если это не было сделано ранее
        if (!viewModel.isDataLoaded) {
            viewModel.currentCurrency.value?.let {
                viewModel.fetchCryptoMarket(it)
            }
            viewModel.isDataLoaded = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCurrencyChangedListener) {
            currencyChangedListener = context
        } else {
            throw RuntimeException("$context must implement OnCurrencyChangedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        currencyChangedListener = null
    }

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

    private fun showLoading(isLoading: Boolean) {
        (requireActivity() as MainActivity).showLoading(isLoading)
    }

    private fun showCryptoList(data: List<CryptoModel>) {
        showLoading(false)
        binding.marketErrorLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE

        adapter.setListData(data, viewModel.currentCurrency.value?.let {
            if (it == "usd") "$" else "₽"
        } ?: "$")
    }

    private fun showError() {
        showLoading(false)
        binding.recyclerView.visibility = View.GONE
        binding.marketErrorLayout.visibility = View.VISIBLE
        binding.btnMarketRetry.setOnClickListener {
            Log.e("crypto", "List trying")
            viewModel.currentCurrency.value?.let { viewModel.fetchCryptoMarket(it) }
        }
    }

    private fun navigateToDetails(cryptoId: String, cryptoTitle: String) {
        val action = CryptoListFragmentDirections.actionCryptoListFragmentToCryptoDetailsFragment(cryptoId, cryptoTitle)
        findNavController().navigate(action)
    }
}

