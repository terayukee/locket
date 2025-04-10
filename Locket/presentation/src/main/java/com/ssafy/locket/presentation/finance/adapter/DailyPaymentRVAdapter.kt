package com.ssafy.locket.presentation.finance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.locket.model.finance.Payment
import com.ssafy.locket.model.finance.payment_history.PaymentDailyHistoryItem
import com.ssafy.locket.model.finance.payment_history.PaymentHistoryItem
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.ItemPaymentBinding
import com.ssafy.locket.presentation.utils.CommonUtils

class DailyPaymentRVAdapter:
    ListAdapter<PaymentDailyHistoryItem, DailyPaymentRVAdapter.CustomViewHolder>(CustomComparator) {
    private lateinit var context: Context

    interface ItemClickListener {
        fun onClick(view: View, data: PaymentDailyHistoryItem, position: Int)
    }

    companion object CustomComparator : DiffUtil.ItemCallback<PaymentDailyHistoryItem>() {
        override fun areItemsTheSame(oldItem: PaymentDailyHistoryItem, newItem: PaymentDailyHistoryItem): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: PaymentDailyHistoryItem, newItem: PaymentDailyHistoryItem): Boolean {
            return oldItem == newItem
        }
    }

    inner class CustomViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PaymentDailyHistoryItem) {
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
            binding.tvReceiptDescription.text = context.getString(R.string.finance_calendar_day_payment_detail, item.category, item.cardName) // TODO 현재는 임의의 day 넣어둠 추후에 변경
            binding.tvReceiptPrice.text = context.getString(R.string.receipt_price, CommonUtils.makeCommaDecimal(item.totalAmount))
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
