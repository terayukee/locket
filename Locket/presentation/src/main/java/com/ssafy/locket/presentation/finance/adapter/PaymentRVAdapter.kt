package com.ssafy.locket.presentation.finance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.locket.CommonUtils
import com.ssafy.locket.model.finance.Payment
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.ItemPaymentBinding

class PaymentRVAdapter(val type: String):
    ListAdapter<Payment, PaymentRVAdapter.CustomViewHolder>(CustomComparator) {
    lateinit var itemClickListener: ItemClickListener
    private lateinit var context: Context

    interface ItemClickListener {
        fun onClick(view: View, data: Payment, position: Int)
    }

    companion object CustomComparator : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem == newItem
        }
    }

    inner class CustomViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Payment) {
            val categoryImg : Int = when(item.category) {
                "shopping" -> R.drawable.ic_finance_category_shopping
                "food" -> R.drawable.ic_finance_category_food
                "cafe" -> R.drawable.ic_finance_category_cafe
                "home" -> R.drawable.ic_finance_category_home
                "transportation" -> R.drawable.ic_finance_category_transportation
                else -> R.drawable.ic_finance_category_etc
            }

            Glide.with(context)
                .load(categoryImg)
                .placeholder(R.drawable.ic_finance_category_etc)
                .into(binding.ivCategory)
            binding.tvReceiptPlace.text = item.place
            binding.tvReceiptDescription.text = context.getString(R.string.receipt_description, item.category, item.cardName, item.date)
            binding.tvReceiptPrice.text = context.getString(R.string.receipt_price, CommonUtils.makeComma(item.price))
            binding.root.setOnClickListener {
                if(type == "receipt") itemClickListener.onClick(it, item, adapterPosition)
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
