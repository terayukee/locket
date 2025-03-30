package com.ssafy.locket.presentation.finance.fragment.payment_list

import com.ssafy.locket.presentation.finance.adapter.PaymentRVAdapter
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.finance.Payment
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentPaymentListBinding

private const val TAG = "PaymentListFragment"
class PaymentListFragment : BaseFragment<FragmentPaymentListBinding>(
    FragmentPaymentListBinding::bind,
    R.layout.fragment_payment_list
) {
    private lateinit var paymentRVAdapter: PaymentRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

    }

    private fun initAdapter() {
        paymentRVAdapter = PaymentRVAdapter("payment")

        binding.rvPayment.apply {
            adapter = paymentRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val tmpList : List<Payment> = listOf(Payment(0,"쿠팡","쇼핑","내일배움카드", 3000, "2024.04.11"), Payment(1,"쿠팡","쇼핑","내일배움카드3", 8000, "2024.04.12"))
        paymentRVAdapter.submitList(tmpList)
    }
}