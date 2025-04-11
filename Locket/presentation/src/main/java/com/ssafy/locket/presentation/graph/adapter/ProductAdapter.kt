package com.ssafy.locket.presentation.graph.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.locket.model.graph.Product
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.ItemProductBinding

class ProductAdapter(var productList: List<Product>, private val navController: NavController, private val action: Int) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){
    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.cvProduct.setOnClickListener {
                val bundle = Bundle().apply { putInt("productId", product.productId)} // 데이터 전달
                navController.navigate(action,bundle)
            }
            binding.tvProductPrice.text = product.currentPrice
            Glide.with(binding.root.context)
                .load(product.imageUrl) // 이미지 URL
                .placeholder(ColorDrawable(Color.WHITE)) // 로딩 중 표시할 이미지
                .error(ColorDrawable(Color.WHITE)) // 로드 실패 시 표시할 이미지
                .into(binding.ivProductImage)
            binding.tvProductDiscount.text = product.discountRate
            binding.tvProductDescription.text = product.productName
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}

