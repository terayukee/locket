package com.ssafy.locket.presentation.graph.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.locket.model.graph.Product
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.ItemMoneyHappyBinding


class MoneyHappyAdapter(var moneyHappyList: List<Product>,private val navController: NavController) : RecyclerView.Adapter<MoneyHappyAdapter.MoneyHappyViewHolder>() {
    inner class MoneyHappyViewHolder(private val binding: ItemMoneyHappyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.cvProduct.setOnClickListener {
                navController.navigate(R.id.action_productListFragment_to_productDetailFragment)
            }
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