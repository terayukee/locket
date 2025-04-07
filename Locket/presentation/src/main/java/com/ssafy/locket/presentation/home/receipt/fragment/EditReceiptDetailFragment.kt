package com.ssafy.locket.presentation.home.receipt.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.home.receipt.ProcessedReceipt
import com.ssafy.locket.model.home.receipt.ReceiptDetail
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentEditReceiptDetailBinding
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
    }

    private fun initAdapter() {
        receiptDetailEditRVAdapter = ReceiptDetailEditRVAdapter()

        binding.rvReceiptDetail.apply {
            adapter = receiptDetailEditRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

//        val tmpList : List<ReceiptDetail> = listOf(
//            ReceiptDetail(0, 15000, "쇼핑", "맑은물에 반모 촌두부 2개입, 300g, 1개", 30),
//            ReceiptDetail(1, 1500, "카페/디저트", "맑은물에 반모 촌두부 2개입, 300g, 1개", 3)
//        )
//        receiptDetailEditRVAdapter.submitList(tmpList)
    }

    private fun initUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            receiptFileSelectionViewModel.receiptDetail.collect { uiState ->
                if(uiState is ReceiptDetailState.Success) {
                    val item = uiState.processReceipt
                    binding.tvStore.text = item.storeName
                    binding.tvTotalPrice.text = getString(R.string.finance_won, CommonUtils.makeComma(item.totalAmount))
                    receiptDetailEditRVAdapter.submitList(item.items)
                }
            }
        }
    }
}