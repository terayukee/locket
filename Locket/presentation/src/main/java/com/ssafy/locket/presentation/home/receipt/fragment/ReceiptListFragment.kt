package com.ssafy.locket.presentation.home.receipt.fragment

import com.ssafy.locket.presentation.finance.adapter.PaymentRVAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.finance.Payment
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentReceiptListBinding

private const val TAG = "ReceiptListFragment"
class ReceiptListFragment : BaseFragment<FragmentReceiptListBinding>(
    FragmentReceiptListBinding::bind,
    R.layout.fragment_receipt_list
) {
    private lateinit var paymentRVAdapter: PaymentRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    private fun initAdapter() {
        paymentRVAdapter = PaymentRVAdapter("receipt")

        binding.rvReceipt.apply {
            adapter = paymentRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        paymentRVAdapter.itemClickListener = object : PaymentRVAdapter.ItemClickListener {
            override fun onClick(view: View, data: Payment, position: Int) {
                findNavController().navigate(R.id.receiptUploadDialog)
            }
        }

        val tmpList : List<Payment> = listOf(
            Payment(0, "쿠팡", "쇼핑", "내일배움카드", 3000, "2024.03.11"),
            Payment(1, "쿠팡", "쇼핑", "내일배움카드3", 8000, "2024.03.10")
        )
        paymentRVAdapter.submitList(tmpList)
    }
}