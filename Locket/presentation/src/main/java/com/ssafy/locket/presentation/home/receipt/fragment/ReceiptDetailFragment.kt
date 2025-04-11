package com.ssafy.locket.presentation.home.receipt.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.home.receipt.ProcessedReceipt
import com.ssafy.locket.model.home.receipt.ReceiptDetail
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentReceiptDetailBinding
import com.ssafy.locket.presentation.home.receipt.adapter.ReceiptDetailRVAdapter
import com.ssafy.locket.presentation.home.receipt.viewmodel.ProcessedReceiptViewModel
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptDetailState
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptFileSelectionViewModel
import com.ssafy.locket.presentation.home.receipt.viewmodel.SavedReceiptState
import com.ssafy.locket.presentation.home.receipt.viewmodel.SelectedReceiptState
import com.ssafy.locket.presentation.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "ReceiptDetailFragment"
@AndroidEntryPoint
class ReceiptDetailFragment : BaseFragment<FragmentReceiptDetailBinding>(
    FragmentReceiptDetailBinding::bind,
    R.layout.fragment_receipt_detail
) {
    private lateinit var receiptDetailRVAdapter: ReceiptDetailRVAdapter
    private val receiptFileSelectionViewModel: ReceiptFileSelectionViewModel by activityViewModels()
    private val processedReceiptViewModel: ProcessedReceiptViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initUI()
    }

    private fun initUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            receiptFileSelectionViewModel.receiptDetail.collect { uiState ->
                if (uiState is ReceiptDetailState.Success) {
                    val item = uiState.processReceipt
                    Log.d(TAG, "initUI: ${item.items}")
                    binding.tvStore.text = item.storeName
                    binding.tvTotalPrice.text =
                        getString(R.string.finance_won, CommonUtils.makeComma(item.totalAmount))
                    receiptDetailRVAdapter.submitList(item.items)
                    binding.btnRegisterReceipt.isEnabled = true
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            processedReceiptViewModel.savedState.collect { uiState ->
                if(uiState is SavedReceiptState.Success) {
                    Log.d(TAG, "initUI: uiState is Success")
                    findNavController().navigate(R.id.action_receiptDetailFragment_to_homeFragment)
                } else {
                    Log.d(TAG, "initUI: uisTate is fail or else ")
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            receiptFileSelectionViewModel.setEditReceipt()
            findNavController().navigate(R.id.action_receiptDetailFragment_to_editReceiptDetailFragment)
        }

        binding.btnRegisterReceipt.setOnClickListener {
            // TODO 영수증 정보 등록하기 api 처리
            receiptFileSelectionViewModel.receiptDetail.value?.let { ocrState ->
                if (ocrState is ReceiptDetailState.Success) {
                    val processedReceipt = ocrState.processReceipt
                    receiptFileSelectionViewModel.selectedReceipt.value?.let { receiptState ->
                        if (receiptState is SelectedReceiptState.Success) {
                            processedReceiptViewModel.saveReceipt(
                                receiptState.transactionId,
                                processedReceipt.storeName,
                                processedReceipt.items,
                                processedReceipt.totalAmount,
                                processedReceipt.categoryAmount
                            )
                        } else {
                            Log.d(TAG, "initUI: no selectedReceipt")
                        }
                    }
                } else {
                    Log.d(TAG, "initUI: no receiptDetailState")
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        context?.let {
            binding.rvReceiptDetail.minimumHeight =
                (resources.displayMetrics.heightPixels * 0.5415).toInt()
        }
    }

    private fun initAdapter() {
        receiptDetailRVAdapter = ReceiptDetailRVAdapter()

        binding.rvReceiptDetail.apply {
            adapter = receiptDetailRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

//        val tmpList : List<ReceiptDetail> = listOf(
//            ReceiptDetail(0, 15000, "쇼핑", "맑은물에 반모 촌두부 2개입, 300g, 1개", 30),
//            ReceiptDetail(1, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3),
//            ReceiptDetail(2, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3),
//            ReceiptDetail(3, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3),
////            ReceiptDetail(4, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3),
////            ReceiptDetail(5, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3),
////            ReceiptDetail(6, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3),
////            ReceiptDetail(7, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3),
////            ReceiptDetail(8, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3),
////            ReceiptDetail(9, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3),
////            ReceiptDetail(10, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3),
////            ReceiptDetail(11, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3),
////            ReceiptDetail(12, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3)
//        )
//        receiptDetailRVAdapter.submitList(tmpList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        receiptViewModel.clearSelectedPayment()
        receiptFileSelectionViewModel.clearSelectedType()
        receiptFileSelectionViewModel.clearSelectedReceiptFile()
    }
}