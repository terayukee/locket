package com.ssafy.locket.presentation.graph.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.locket.model.graph.Product
import com.ssafy.locket.model.graph.ProductxInfo
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.ItemMoneyHappyBinding
import com.ssafy.locket.presentation.utils.CommonUtils


class MoneyHappyAdapter(var moneyHappyList: List<Product>, private val navController: NavController) : RecyclerView.Adapter<MoneyHappyAdapter.MoneyHappyViewHolder>() {
    inner class MoneyHappyViewHolder(private val binding: ItemMoneyHappyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.cvProduct.setOnClickListener {
                val bundle = Bundle().apply { putInt("productId", product.productId)} // 데이터 전달
                navController.navigate(R.id.action_productListFragment_to_productDetailFragment,bundle)
            }
            binding.tvProductName.text = product.productName
            binding.tvDiscountRate.text= product.discountRate
            binding.tvPrice.text= product.currentPrice
            Glide.with(binding.root.context)
                .load(product.imageUrl) // 이미지 URL
                .placeholder(R.drawable.ic_all_empty_heart) // 로딩 중 표시할 이미지
                .error(R.drawable.ic_all_empty_heart) // 로드 실패 시 표시할 이미지
                .into(binding.ivProduct)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoneyHappyViewHolder {
        val binding = ItemMoneyHappyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoneyHappyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoneyHappyViewHolder, position: Int) {
        holder.bind(moneyHappyList[position])
    }

    override fun getItemCount(): Int {
        return moneyHappyList.size
    }
}