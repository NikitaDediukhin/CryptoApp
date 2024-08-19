package com.ndproject.cryptoapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ndproject.cryptoapp.activity.MainActivity
import com.ndproject.cryptoapp.databinding.FragmentCryptoDetailsBinding
import com.ndproject.cryptoapp.viewmodel.CryptoViewModel
import com.ndproject.domain.model.CryptoDetailsModel
import com.ndproject.domain.utils.DataState

/**
 * Фрагмент для отображения подробной информации о криптовалюте.
 * Загружает и отображает данные криптовалюты, обрабатывает ошибки и обновления UI.
 */
class CryptoDetailsFragment : Fragment() {
    private lateinit var binding: FragmentCryptoDetailsBinding
    private val viewModel: CryptoViewModel by activityViewModels()
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

        // Проверка и загрузка данных о криптовалюте, если они ещё не загружены
        if(!viewModel.isDetailsDataLoaded){
            val cryptoId = args.cryptoId
            viewModel.fetchCryptoDetails(cryptoId)
            viewModel.isDetailsDataLoaded = true
        }
    }

    private fun setupObservers() {
        viewModel.cryptoDetailsLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataState.Loading -> {
                    showLoading(true)
                    binding.detailsContainer.visibility = View.GONE
                    binding.detailsErrorLayout.visibility = View.GONE
                }
                is DataState.Success -> {
                    showDetails(state.data)
                }
                is DataState.Error -> {
                    showError()
                }
            }
        }

    }

    private fun showDetails(data: CryptoDetailsModel){
        // Скрыть индикатор загрузки и показать данные
        showLoading(false)
        binding.detailsErrorLayout.visibility = View.GONE
        binding.detailsContainer.visibility = View.VISIBLE
        // Загрузка изображения и данных криптовалюты
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
        // Скрыть данные и показывать сообщение об ошибке
        showLoading(false)
        binding.detailsContainer.visibility = View.GONE
        binding.detailsErrorLayout.visibility = View.VISIBLE
        binding.btnDetailsRetry.setOnClickListener {
            // Попытка повторной загрузки данных при ошибке
            viewModel.fetchCryptoDetails(args.cryptoId)
        }
    }

}