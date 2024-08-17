package com.ndproject.cryptoapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ndproject.cryptoapp.R
import com.ndproject.cryptoapp.activity.MainActivity
import com.ndproject.cryptoapp.databinding.FragmentCryptoDetailsBinding
import com.ndproject.cryptoapp.databinding.FragmentCryptoListBinding
import com.ndproject.cryptoapp.viewmodel.CryptoViewModel
import com.ndproject.domain.model.CryptoDetailsModel
import com.ndproject.domain.utils.DataState
import java.io.Serializable

class CryptoDetailsFragment : Fragment() {
    private lateinit var binding: FragmentCryptoDetailsBinding
    val viewModel: CryptoViewModel by activityViewModels()
    private val args: CryptoDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCryptoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        val cryptoId = args.cryptoId
        viewModel.fetchCryptoDetails(cryptoId)
    }

    private fun setupObservers() {
        viewModel.cryptoDetailsLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Loading -> {
                    showLoading(true)
                    binding.detailsContainer.visibility = View.GONE
                }
                is DataState.Success -> {
                    showLoading(false)
                    binding.detailsContainer.visibility = View.VISIBLE
                    showDetails(state.data)
                }
                is DataState.Error -> {
                    showLoading(false)
                    binding.detailsContainer.visibility = View.GONE
                    showError()
                }
            }
        }

    }

    private fun showDetails(data: CryptoDetailsModel){
        context?.let {
            Glide.with(it)
                .load(data.image)
                .into(binding.ivCryptoDetailsLogo)
        }
        binding.tvDetails.text = data.description
        binding.tvCategories.text = data.categories.toString()
    }

    private fun showLoading(isLoading: Boolean) {
        (requireActivity() as MainActivity).showLoading(isLoading)
    }

    private fun showError() {
        val bundle = Bundle().apply {
            putString("actionType", "fetchCryptoDetails")
            putString("cryptoId", args.cryptoId)
        }
        findNavController().navigate(R.id.errorFragment, bundle)
    }

}