package com.ssafy.locket.ui.graph.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.locket.data.remote.dto.Product
import com.ssafy.locket.databinding.ItemMoneyHappyBinding
import com.ssafy.locket.databinding.ItemProductBinding

class MoneyHappyAdapter(var moneyHappyList: List<Product>) : RecyclerView.Adapter<MoneyHappyAdapter.MoneyHappyViewHolder>() {
    inner class MoneyHappyViewHolder(private val binding: ItemMoneyHappyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {

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