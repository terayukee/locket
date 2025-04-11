package com.ssafy.locket.presentation.home.receipt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.locket.model.home.receipt.Receipt
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.ItemPaymentBinding
import com.ssafy.locket.presentation.utils.CommonUtils

class AvailableReceiptAdapter(): ListAdapter<Receipt, AvailableReceiptAdapter.CustomViewHolder>(CustomComparator) {
    lateinit var itemClickListener: ItemClickListener
    private lateinit var context: Context

    interface ItemClickListener {
        fun onClick(view: View, data: Receipt, position: Int)
    }

    companion object CustomComparator : DiffUtil.ItemCallback<Receipt>() {
        override fun areItemsTheSame(oldItem: Receipt, newItem: Receipt): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: Receipt, newItem: Receipt): Boolean {
            return oldItem == newItem
        }
    }

    inner class CustomViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Receipt) {
            val categoryImg : Int = when(item.category) {
                "쇼핑" -> R.drawable.ic_finance_category_shopping
                "식비" -> R.drawable.ic_finance_category_food
                "카페/디저트" -> R.drawable.ic_finance_category_cafe
                "생활" -> R.drawable.ic_finance_category_home
                "교통" -> R.drawable.ic_finance_category_transportation
                else -> R.drawable.ic_finance_category_etc
            }

            Glide.with(context)
                .load(categoryImg)
                .placeholder(R.drawable.ic_finance_category_etc)
                .into(binding.ivCategory)
            binding.tvReceiptPlace.text = item.storeName
            binding.tvReceiptDescription.text = context.getString(
                R.string.receipt_description,
                item.category,
                item.cardName,
                item.paymentDate
            )
            binding.tvReceiptPrice.text =
                context.getString(R.string.receipt_price, CommonUtils.makeComma(item.amount))
            binding.root.setOnClickListener {
                itemClickListener.onClick(it, item, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context = parent.context
        val binding =
            ItemPaymentBinding.inflate(LayoutInflater.from(context), parent, false)

        val displayMetrics = context.resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val itemHeight = (screenHeight * 0.08).toInt()
        binding.root.layoutParams.height = itemHeight
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}