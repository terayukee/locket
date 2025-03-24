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
import com.ssafy.locket.ui.home.receipt.adapter.ReceiptListRVAdapter

private const val TAG = "ReceiptListFragment"
class ReceiptListFragment : BaseFragment<FragmentReceiptListBinding>(
    FragmentReceiptListBinding::bind,
    R.layout.fragment_receipt_list
) {
    private lateinit var receiptListRVAdapter: ReceiptListRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    private fun initAdapter() {
        receiptListRVAdapter = ReceiptListRVAdapter()

        binding.rvReceipt.apply {
            adapter = receiptListRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        receiptListRVAdapter.itemClickListener = object : ReceiptListRVAdapter.ItemClickListener {
            override fun onClick(view: View, data: Receipt, position: Int) {
                Log.d(TAG, "onClick: ${data.place} $position")
                findNavController().navigate(R.id.receiptUploadDialog)
            }
        }

        val tmpList : List<Receipt> = listOf(Receipt(0,"쿠팡","쇼핑","내일배움카드", 3000), Receipt(1,"쿠팡","쇼핑","내일배움카드3", 8000))
        receiptListRVAdapter.submitList(tmpList)
    }
}