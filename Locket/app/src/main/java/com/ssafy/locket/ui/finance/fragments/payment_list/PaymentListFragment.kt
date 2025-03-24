package com.ssafy.locket.ui.finance.fragments.payment_list

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.data.model.dto.Receipt
import com.ssafy.locket.databinding.FragmentPaymentListBinding
import com.ssafy.locket.ui.home.receipt.adapter.ReceiptRVAdapter

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

//        receiptRVAdapter.itemClickListener = object : ReceiptRVAdapter.ItemClickListener {
//            override fun onClick(view: View, data: Receipt, position: Int) {
//                Log.d(TAG, "onClick in paymentListFragment: ${data.place} $position")
////                findNavController().navigate(R.id.receiptUploadDialog)
//            }
//        }

        val tmpList : List<Receipt> = listOf(Receipt(0,"쿠팡","쇼핑","내일배움카드", 3000), Receipt(1,"쿠팡","쇼핑","내일배움카드3", 8000))
        receiptRVAdapter.submitList(tmpList)
    }
}