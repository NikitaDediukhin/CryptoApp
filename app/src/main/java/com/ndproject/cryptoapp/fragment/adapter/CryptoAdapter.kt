package com.ndproject.cryptoapp.fragment.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ndproject.cryptoapp.R
import com.ndproject.domain.model.CryptoModel
import java.text.NumberFormat
import java.util.Locale

class CryptoAdapter(
    // Обработчик кликов по элементам списка
    private val onClickItem: (id: String, name: String) -> Unit
) : RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder>() {

    // Список криптовалют для отображения
    private var cryptoList: List<CryptoModel> = listOf()
    // Текущий символ валюты для отображения цен
    private var currencySymbol: String = "$"

    @SuppressLint("NotifyDataSetChanged")
    fun setListData(list: List<CryptoModel>, currency: String) {
        cryptoList = list
        currencySymbol = currency
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.crypto_item, parent, false)
        return CryptoViewHolder(itemView)
    }

    override fun getItemCount(): Int = cryptoList.size

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        val currentItem = cryptoList[position]
        holder.onBind(currentItem, currencySymbol, onClickItem)
    }

    class CryptoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivLogo: ImageView = itemView.findViewById(R.id.ivCryptoLogo)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvCryptoName)
        private val tvTicker: TextView = itemView.findViewById(R.id.tvCryptoTicker)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvCryptoPrice)
        private val tvPriceChange: TextView = itemView.findViewById(R.id.tvCryptoChange)

        /**
         * @param cryptoItem Объект криптовалюты для отображения
         * @param currencySymbol Символ валюты для отображения цены
         * @param onClickItem Обработчик кликов по элементам списка
         */
        fun onBind(
            cryptoItem: CryptoModel,
            currencySymbol: String,
            onClickItem: (id: String, name: String) -> Unit
        ) {
            // Загрузка изображения с помощью Glide
            Glide.with(itemView.context)
                .load(cryptoItem.image)
                .into(ivLogo)

            // Установка названия и тикера криптовалюты
            tvTitle.text = cryptoItem.name
            tvTicker.text = cryptoItem.symbol.uppercase()

            // Форматирование и установка цены
            val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
                maximumFractionDigits = 2
                minimumFractionDigits = 2
            }
            val formattedPrice = numberFormat.format(cryptoItem.currentPrice)
            tvPrice.text = String.format("%s%s", currencySymbol, formattedPrice)

            // Форматирование изменения цены
            val priceChangeText = String.format(Locale.US, "%+.2f%%", cryptoItem.priceChange)
            tvPriceChange.text = priceChangeText

            // Установка цвета изменения цены
            val color = if (cryptoItem.priceChange >= 0) R.color.teal else R.color.red
            tvPriceChange.setTextColor(ContextCompat.getColor(itemView.context, color))

            // Обработчик клика по элементу списка
            itemView.setOnClickListener {
                onClickItem(cryptoItem.id, cryptoItem.name)
            }
        }
    }
}