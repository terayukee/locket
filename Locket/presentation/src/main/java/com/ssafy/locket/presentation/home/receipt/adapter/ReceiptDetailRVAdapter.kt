package com.ssafy.locket.presentation.home.receipt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.locket.model.home.receipt.ReceiptDetail
import com.ssafy.locket.presentation.databinding.ItemReceiptBinding
import com.ssafy.locket.presentation.utils.CommonUtils

class ReceiptDetailRVAdapter: ListAdapter<ReceiptDetail, ReceiptDetailRVAdapter.CustomViewHolder>(CustomComparator) {
    private lateinit var context: Context

    companion object CustomComparator : DiffUtil.ItemCallback<ReceiptDetail>() {
        override fun areItemsTheSame(oldItem: ReceiptDetail, newItem: ReceiptDetail): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(oldItem: ReceiptDetail, newItem: ReceiptDetail): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptDetailRVAdapter.CustomViewHolder {
        context = parent.context
        val binding = ItemReceiptBinding.inflate(LayoutInflater.from(context), parent, false)
        return CustomViewHolder(binding)
    }

    inner class CustomViewHolder(private val binding: ItemReceiptBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReceiptDetail) {
            binding.tvProductName.text = item.itemName
            binding.tvUnitPrice.text = CommonUtils.makeComma(item.itemAmount)
            binding.tvCount.text = item.itemQuantity.toString()
            if(item.itemCategory=="카페/디저트"){
                binding.tvCategory.text = "카페"
            }
            else{
                binding.tvCategory.text = item.itemCategory
            }
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
