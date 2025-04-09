package com.ssafy.locket.presentation.home.receipt.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.locket.model.home.receipt.Receipt
import com.ssafy.locket.model.home.receipt.ReceiptDetail
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.ItemReceiptEditBinding
import com.ssafy.locket.presentation.home.receipt.adapter.AvailableReceiptAdapter.ItemClickListener
import com.ssafy.locket.presentation.utils.CommonUtils

private const val TAG = "ReceiptDetailEditRVAdap"

class ReceiptDetailEditRVAdapter :
    ListAdapter<ReceiptDetail, ReceiptDetailEditRVAdapter.CustomViewHolder>(
        ReceiptDetailRVAdapter
    ) {
    lateinit var itemClickListener: ItemClickListener

    private val categoryType = arrayOf("식비", "카페", "생활", "쇼핑", "교통", "기타")
    private lateinit var context: Context

    interface ItemClickListener {
        fun onClick(view: View, data: ReceiptDetail, position: Int)
    }

    inner class CustomViewHolder(private val binding: ItemReceiptEditBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReceiptDetail) {
            var isInitialized = false

            binding.etProductName.text = item.itemName
            binding.tvUnitPrice.text = CommonUtils.makeComma(item.itemAmount)
            binding.tvCount.text = item.itemQuantity.toString()
            if (item.itemCategory == "카페/디저트") {
                binding.tvCategoryDropdown.text = "카페"
            } else {
                binding.tvCategoryDropdown.text = item.itemCategory
            }
            binding.tvCategoryDropdown.setOnClickListener {
                // 👉 단가, 수량 입력창 포커스 해제
//                binding.etUnitPrice.clearFocus()
//                binding.etCount.clearFocus()
//                val imm =
//                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
//                imm.hideSoftInputFromWindow(binding.etUnitPrice.windowToken, 0)
                val popupView =
                    LayoutInflater.from(context).inflate(R.layout.drop_down_listview, null)
                val popupWindow = PopupWindow(
                    popupView,
                    binding.tvCategoryDropdown.width,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
                )
                val listView = popupView.findViewById<ListView>(R.id.listView)

                listView.adapter =
                    ArrayAdapter(context, android.R.layout.simple_list_item_1, categoryType)
                listView.setOnItemClickListener { _, _, position, _ ->
                    val selected = categoryType[position]
                    binding.tvCategoryDropdown.text = selected
//                    item.itemCategory = selected
                    val updatedItem = item.copy(itemCategory = selected)
                    itemClickListener.onClick(
                        binding.tvCategoryDropdown,
                        updatedItem,
                        adapterPosition
                    )
                    popupWindow.dismiss()
                }
                popupWindow.isOutsideTouchable = true
                popupWindow.elevation = 10f
                popupWindow.showAsDropDown(binding.tvCategoryDropdown)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context = parent.context
        val binding = ItemReceiptEditBinding.inflate(LayoutInflater.from(context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
