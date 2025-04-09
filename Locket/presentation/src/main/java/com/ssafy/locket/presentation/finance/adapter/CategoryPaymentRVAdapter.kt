package com.ssafy.locket.presentation.finance.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.locket.model.finance.CategoryPayment
import com.ssafy.locket.model.finance.budget.feedback.CategoryBreakdown
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.ItemCategoryPaymentBinding
import com.ssafy.locket.presentation.utils.CommonUtils

class CategoryPaymentRVAdapter: ListAdapter<CategoryBreakdown, CategoryPaymentRVAdapter.CustomViewHolder>(
    CategoryPaymentRVAdapter
) {
    private lateinit var context: Context

    companion object CustomComparator : DiffUtil.ItemCallback<CategoryBreakdown>() {
        override fun areItemsTheSame(oldItem: CategoryBreakdown, newItem: CategoryBreakdown): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: CategoryBreakdown, newItem: CategoryBreakdown): Boolean {
            return oldItem == newItem
        }
    }

    inner class CustomViewHolder(private val binding: ItemCategoryPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryBreakdown) {
            var bulletColor = Color.parseColor("#9E9E9E")
            var categoryName = "기타"

            when(item.category) {
                "쇼핑" -> {
                    bulletColor = Color.parseColor("#EB4634")
                    categoryName = "쇼핑"
                }
                "식비" -> {
                    bulletColor = Color.parseColor("#8E9FE2")
                    categoryName = "식비"
                }
                "카페/디저트" -> {
                    bulletColor = Color.parseColor("#FFB602")
                    categoryName = "카페/디저트"
                }
                "생활" -> {
                    bulletColor = Color.parseColor("#C59A6E")
                    categoryName = "생활"
                }
                "교통" -> {
                    bulletColor = Color.parseColor("#8D73A8")
                    categoryName = "교통"
                }
                "기타" -> {
                    bulletColor = Color.parseColor("#9E9E9E")
                    categoryName = "기타"
                }
            }

            binding.ivBullet.setColorFilter(bulletColor, PorterDuff.Mode.SRC_IN)

            binding.tvCategory.text = categoryName
            binding.tvCategoryPercent.text = context.resources.getString(R.string.finance_analysis_percent, item.percentage)
            binding.tvPaymentAmount.text = context.resources.getString(R.string.finance_won, CommonUtils.makeCommaDecimal(item.amount))

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context = parent.context
        val binding =
            ItemCategoryPaymentBinding.inflate(LayoutInflater.from(context), parent, false)

//        val displayMetrics = context.resources.displayMetrics
//        val screenHeight = displayMetrics.heightPixels
//        val itemHeight = (screenHeight * 0.0224).toInt()
//        binding.root.layoutParams.height = itemHeight
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}