package com.ssafy.locket.presentation.home.receipt.adapter

import android.R
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.locket.model.home.ReceiptDetail
import com.ssafy.locket.presentation.databinding.ItemReceiptEditBinding
import com.ssafy.locket.presentation.utils.CommonUtils

private const val TAG = "ReceiptDetailEditRVAdap"
class ReceiptDetailEditRVAdapter: ListAdapter<ReceiptDetail, ReceiptDetailEditRVAdapter.CustomViewHolder>(
    ReceiptDetailRVAdapter
) {
    private val categoryType = arrayOf("식비", "카페/디저트", "생활", "쇼핑", "교통", "기타")
    private lateinit var context: Context

    inner class CustomViewHolder(private val binding: ItemReceiptEditBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReceiptDetail) {
            binding.etProductName.text = item.itemName
            binding.etUnitPrice.hint = CommonUtils.makeComma(item.itemAmount)
            binding.etCount.hint = item.itemQuantity.toString()

            val spinnerAdapter = ArrayAdapter(context, R.layout.simple_spinner_item, categoryType)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spCategory.adapter = spinnerAdapter

            binding.spCategory.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedItem = parent?.getItemAtPosition(position)
                        Log.d(TAG, "onItemSelected: $selectedItem")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // empty here
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptDetailEditRVAdapter.CustomViewHolder {
        context = parent.context
        val binding = ItemReceiptEditBinding.inflate(LayoutInflater.from(context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceiptDetailEditRVAdapter.CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}