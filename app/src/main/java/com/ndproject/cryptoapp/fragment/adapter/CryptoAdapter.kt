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
    private val onClickItem: (id: String, name: String) -> Unit
) : RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder>() {

    private var cryptoList: List<CryptoModel> = listOf()
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

        fun onBind(
            cryptoItem: CryptoModel,
            currencySymbol: String,
            onClickItem: (id: String, name: String) -> Unit
        ) {
            Glide.with(itemView.context)
                .load(cryptoItem.image)
                .into(ivLogo)

            tvTitle.text = cryptoItem.name
            tvTicker.text = cryptoItem.symbol.uppercase()

            val numberFormat = NumberFormat.getNumberInstance(Locale.US)
            numberFormat.maximumFractionDigits = 2
            numberFormat.minimumFractionDigits = 2

            val formattedPrice = numberFormat.format(cryptoItem.currentPrice)

            tvPrice.text = String.format("%s%s", currencySymbol, formattedPrice)

            val priceChangeText = if (cryptoItem.priceChange >= 0) {
                "+${String.format(Locale.US, "%.2f%%", cryptoItem.priceChange)}"
            } else {
                String.format(Locale.US, "%.2f%%", cryptoItem.priceChange)
            }
            tvPriceChange.text = priceChangeText

            val color = if (cryptoItem.priceChange >= 0) {
                R.color.teal
            } else {
                R.color.red
            }
            tvPriceChange.setTextColor(ContextCompat.getColor(itemView.context, color))

            itemView.setOnClickListener {
                onClickItem(cryptoItem.id, cryptoItem.name)
            }
        }
    }
}