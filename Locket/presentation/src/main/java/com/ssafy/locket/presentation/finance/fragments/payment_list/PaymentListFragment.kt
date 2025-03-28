package com.ssafy.locket.presentation.finance.fragments.payment_list

import ReceiptRVAdapter
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.finance.Receipt
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentPaymentListBinding

private const val TAG = "PaymentListFragment"
class PaymentListFragment : BaseFragment<FragmentPaymentListBinding>(
    FragmentPaymentListBinding::bind,
    R.layout.fragment_payment_list
) {
    private lateinit var receiptRVAdapter: ReceiptRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

    }

    private fun initAdapter() {
        receiptRVAdapter = ReceiptRVAdapter("payment")

        binding.rvPayment.apply {
            adapter = receiptRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val tmpList : List<Receipt> = listOf(Receipt(0,"쿠팡","쇼핑","내일배움카드", 3000, "2024.04.11"), Receipt(1,"쿠팡","쇼핑","내일배움카드3", 8000, "2024.04.12"))
        receiptRVAdapter.submitList(tmpList)
    }
}