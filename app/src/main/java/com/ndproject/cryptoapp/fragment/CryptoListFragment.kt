package com.ndproject.cryptoapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ndproject.cryptoapp.R
import com.ndproject.cryptoapp.activity.MainActivity
import com.ndproject.cryptoapp.databinding.FragmentCryptoListBinding
import com.ndproject.cryptoapp.fragment.adapter.CryptoAdapter
import com.ndproject.cryptoapp.viewmodel.CryptoViewModel
import com.ndproject.domain.model.CryptoModel
import com.ndproject.domain.utils.DataState
import java.io.Serializable

class CryptoListFragment : Fragment() {

    private lateinit var binding: FragmentCryptoListBinding
    val viewModel: CryptoViewModel by activityViewModels()
    private lateinit var adapter: CryptoAdapter

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

        viewModel.currentCurrency.value?.let { viewModel.fetchCryptoMarket(it) }
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
        viewModel.currentCurrency.observe(viewLifecycleOwner) { currency ->
            viewModel.fetchCryptoMarket(currency)
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

