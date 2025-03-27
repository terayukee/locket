package com.ssafy.locket.presentation.home.receipt

import ReceiptRVAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.finance.Receipt
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentReceiptListBinding

private const val TAG = "ReceiptListFragment"
class ReceiptListFragment : BaseFragment<FragmentReceiptListBinding>(
    FragmentReceiptListBinding::bind,
    R.layout.fragment_receipt_list
) {
    private lateinit var receiptRVAdapter: ReceiptRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val tmpList : List<Receipt> = listOf(Receipt(0,"쿠팡","쇼핑","내일배움카드", 3000, "2024.03.11"), Receipt(1,"쿠팡","쇼핑","내일배움카드3", 8000, "2024.03.10"))
        receiptRVAdapter.submitList(tmpList)
    }
}