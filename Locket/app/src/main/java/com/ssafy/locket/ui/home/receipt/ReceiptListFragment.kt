package com.ssafy.locket.ui.home.receipt

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.data.model.dto.Receipt
import com.ssafy.locket.databinding.FragmentReceiptListBinding
import com.ssafy.locket.ui.home.receipt.adapter.ReceiptRVAdapter

private const val TAG = "ReceiptListFragment"
class ReceiptListFragment : BaseFragment<FragmentReceiptListBinding>(
    FragmentReceiptListBinding::bind,
    R.layout.fragment_receipt_list
) {
    private lateinit var receiptRVAdapter: ReceiptRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initAdapter()
    }

    private fun initAdapter() {
        receiptRVAdapter = ReceiptRVAdapter("receipt")

        binding.rvReceipt.apply {
            adapter = receiptRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        receiptRVAdapter.itemClickListener = object : ReceiptRVAdapter.ItemClickListener {
            override fun onClick(view: View, data: Receipt, position: Int) {
                Log.d(TAG, "onClick: ${data.place} $position")
                findNavController().navigate(R.id.receiptUploadDialog)
            }
        }

        val tmpList : List<Receipt> = listOf(Receipt(0,"쿠팡","쇼핑","내일배움카드", 3000), Receipt(1,"쿠팡","쇼핑","내일배움카드3", 8000))
        receiptRVAdapter.submitList(tmpList)
    }

    fun initEvent(){
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}