package com.ssafy.locket.presentation.home.receipt.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.home.character.Gifticon
import com.ssafy.locket.model.home.receipt.ProcessedReceipt
import com.ssafy.locket.model.home.receipt.ReceiptDetail
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentEditReceiptDetailBinding
import com.ssafy.locket.presentation.home.character.adapter.GifticonRVAdapter
import com.ssafy.locket.presentation.home.receipt.adapter.ReceiptDetailEditRVAdapter
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptDetailState
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptFileSelectionViewModel
import com.ssafy.locket.presentation.utils.CommonUtils
import kotlinx.coroutines.launch

class EditReceiptDetailFragment : BaseFragment<FragmentEditReceiptDetailBinding>(
    FragmentEditReceiptDetailBinding::bind,
    R.layout.fragment_edit_receipt_detail
) {
    private lateinit var receiptDetailEditRVAdapter: ReceiptDetailEditRVAdapter
    private val receiptFileSelectionViewModel : ReceiptFileSelectionViewModel by activityViewModels()

    private var processedReceipt: ProcessedReceipt? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        initUI()
        context?.let {
            binding.rvReceiptDetail.minimumHeight = (resources.displayMetrics.heightPixels * 0.5415).toInt()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnConfirm.setOnClickListener {
//            val receiptDetail = (receiptFileSelectionViewModel.receiptDetail.value as? ReceiptDetailState.Success)?.processReceipt
//            receiptFileSelectionViewModel.updateReceipt(ReceiptDetailState.Success(ProcessedReceipt(receiptDetail.storeName, )))
            findNavController().navigate(R.id.action_editReceiptDetailFragment_to_receiptDetailFragment)
        }

        binding.rvReceiptDetail.setOnTouchListener { v, event ->
            // 포커스된 뷰가 있으면 포커스 제거 및 키보드 내림
            val currentFocus = activity?.currentFocus
            if (currentFocus != null) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
                currentFocus.clearFocus()
            }
            false
        }
    }

    private fun initAdapter() {
        receiptDetailEditRVAdapter = ReceiptDetailEditRVAdapter()

        binding.rvReceiptDetail.apply {
            adapter = receiptDetailEditRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        receiptDetailEditRVAdapter.itemClickListener = object : ReceiptDetailEditRVAdapter.ItemClickListener {
            override fun onClick(view: View, data: ReceiptDetail, position: Int) {
                // ✅ ViewModel에 반영하지 않고, 로컬의 processedReceipt만 업데이트
                processedReceipt = processedReceipt?.copy(
                    items = processedReceipt?.items?.toMutableList()?.apply {
                        set(position, data.copy())
                    } ?: listOf()
                )
            }
        }
//        receiptDetailEditRVAdapter.itemClickListener = object : ReceiptDetailEditRVAdapter.ItemClickListener {
//            override fun onClick(view: View, data: String, position: Int) {
//                val newItem = processedReceipt?.items?.get(position)?.copy(
//                    itemCategory = data.itemCategory,
//                    itemAmount = data.itemAmount,
//                    itemQuantity = data.itemQuantity,
//                    itemName = data.itemName)
//
//                val updatedReceipt = processedReceipt?.let { receipt ->
//                    val updatedItems = receipt.items.toMutableList().apply {
//                        newItem?.let { set(position, it) }
//                    }
//
//                    receipt.copy(
//                        categoryAmount = receipt.categoryAmount,
//                        totalAmount = receipt.totalAmount,
//                        storeName = receipt.storeName,
//                        items = updatedItems
//                    )
//                }
//                updatedReceipt?.let {
//                    receiptFileSelectionViewModel.setReceiptDetail(it)
//                }
//            }
//        }
    }

    private fun initUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            receiptFileSelectionViewModel.receiptDetail.collect { uiState ->
                if(uiState is ReceiptDetailState.Success) {
                    val item = uiState.processReceipt
                    processedReceipt = item
                    binding.tvStore.text = item.storeName
                    binding.tvTotalPrice.text = getString(R.string.finance_won, CommonUtils.makeComma(item.totalAmount))
                    receiptDetailEditRVAdapter.submitList(item.items)
                }
            }
        }
    }
}