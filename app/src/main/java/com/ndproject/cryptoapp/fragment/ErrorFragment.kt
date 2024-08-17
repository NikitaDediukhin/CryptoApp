package com.ndproject.cryptoapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ndproject.cryptoapp.activity.MainActivity
import com.ndproject.cryptoapp.databinding.FragmentErrorBinding
import com.ndproject.cryptoapp.viewmodel.CryptoViewModel
import com.ndproject.domain.utils.DataState

class ErrorFragment : Fragment() {

    private lateinit var binding: FragmentErrorBinding
    val viewModel: CryptoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        // Register for fragment result listener
        parentFragmentManager.setFragmentResultListener("retryAction", viewLifecycleOwner) { requestKey, bundle ->
            handleRetry()
        }
        binding.retryButton.setOnClickListener {
            Log.e("retryButton", "Кнопочка прожимается")
            handleRetry()
        }

    }

    private fun handleRetry() {
        val actionType = arguments?.getString("actionType")
        when (actionType) {
            "fetchCryptoMarket" -> {
                // Retry fetching the crypto market
                (parentFragment as? CryptoListFragment)?.let {
                    it.viewModel.currentCurrency.value?.let { currency ->
                        Log.e("retryButton", "И действие работает (list)")
                        it.viewModel.fetchCryptoMarket(currency)
                    }
                }
            }
            "fetchCryptoDetails" -> {
                // Retry fetching crypto details
                val cryptoId = arguments?.getString("cryptoId")
                cryptoId?.let {
                    Log.e("retryButton", "И действие работает (desc)")
                    (parentFragment as? CryptoDetailsFragment)?.viewModel?.fetchCryptoDetails(it)
                }
            }
        }
        parentFragmentManager.popBackStack()
    }


    private fun setupObservers() {
        viewModel.cryptoListLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Loading -> {
                    showLoading(true)
                    binding.errorLayout.visibility = View.GONE
                }
                is DataState.Success -> {
                    showLoading(false)
                    parentFragmentManager.popBackStack()
                }
                is DataState.Error -> {
                    showLoading(false)
                    binding.errorLayout.visibility = View.VISIBLE
                    Log.e("crypto", state.message)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        (requireActivity() as MainActivity).showLoading(isLoading)
    }
}
