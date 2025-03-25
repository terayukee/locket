package com.ssafy.locket.ui.finance.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.locket.CommonUtils
import com.ssafy.locket.R
import com.ssafy.locket.data.model.dto.CategoryPayment
import com.ssafy.locket.databinding.ItemCategoryPaymentBinding

class CategoryPaymentRVAdapter: ListAdapter<CategoryPayment, CategoryPaymentRVAdapter.CustomViewHolder>(
    CategoryPaymentRVAdapter
) {
    private lateinit var context: Context

    companion object CustomComparator : DiffUtil.ItemCallback<CategoryPayment>() {
        override fun areItemsTheSame(oldItem: CategoryPayment, newItem: CategoryPayment): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: CategoryPayment, newItem: CategoryPayment): Boolean {
            return oldItem == newItem
        }
    }

    inner class CustomViewHolder(private val binding: ItemCategoryPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryPayment) {
            var bulletColor = Color.parseColor("#8D73A8")
            var categoryName = "기타"

            when(item.category) {
                "shopping" -> {
                    bulletColor = Color.parseColor("#EB4634")
                    categoryName = "쇼핑"
                }
                "food" -> {
                    bulletColor = Color.parseColor("#8E9FE2")
                    categoryName = "식비"
                }
                "cafe" -> {
                    bulletColor = Color.parseColor("#FFB602")
                    categoryName = "카페/간식"
                }
                "home" -> {
                    bulletColor = Color.parseColor("#C59A6E")
                    categoryName = "생활"
                }
                "transportation" -> {
                    bulletColor = Color.parseColor("#8D73A8")
                    categoryName = "교통"
                }
            }

            binding.ivBullet.setBackgroundColor(bulletColor)
            // 다른 거 찾아보기, 네모네모해짐

            binding.tvCategory.text = categoryName
            binding.tvCategoryPercent.text = context.resources.getString(R.string.finance_analysis_percent, item.percent)
            binding.tvPaymentAmount.text = context.resources.getString(R.string.finance_won, CommonUtils.makeComma(item.amount))

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context = parent.context
        val binding =
            ItemCategoryPaymentBinding.inflate(LayoutInflater.from(context), parent, false)

        val displayMetrics = context.resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val itemHeight = (screenHeight * 0.0224).toInt()
        binding.root.layoutParams.height = itemHeight
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}