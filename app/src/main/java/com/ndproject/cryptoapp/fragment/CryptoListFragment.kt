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

        // Настройка RecyclerView
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
                    (requireActivity() as MainActivity).showLoading(true)
                    binding.recyclerView.visibility = View.GONE
                }
                is DataState.Success -> {
                    (requireActivity() as MainActivity).showLoading(false)
                    if (state.data.isEmpty()) {
                        showError()
                    } else {
                        binding.recyclerView.visibility = View.VISIBLE
                        showCryptoList(state.data)
                    }
                }
                is DataState.Error -> {
                    (requireActivity() as MainActivity).showLoading(false)
                    Log.e("crypto", state.message)
                    showError()
                }
            }
        }
        viewModel.currentCurrency.observe(viewLifecycleOwner) { currency ->
            viewModel.fetchCryptoMarket(currency)
        }
    }

    private fun showCryptoList(data: List<CryptoModel>) {
        (requireActivity() as MainActivity).showLoading(false)
        binding.recyclerView.visibility = View.VISIBLE
        adapter.setListData(data, viewModel.currentCurrency.value?.let {
            if (it == "usd") "$" else "₽"
        } ?: "$")
    }

    private fun showError() {
        val bundle = Bundle().apply {
            putString("actionType", "fetchCryptoMarket")
        }
        findNavController().navigate(R.id.errorFragment, bundle)
    }

    private fun navigateToDetails(cryptoId: String, cryptoTitle: String) {
        val action = CryptoListFragmentDirections.actionCryptoListFragmentToCryptoDetailsFragment(cryptoId, cryptoTitle)
        findNavController().navigate(action)
    }
}

