package com.ssafy.locket.presentation.finance.fragment.payment_list

import com.ssafy.locket.presentation.finance.adapter.PaymentRVAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.finance.Payment
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentPaymentListBinding
import com.ssafy.locket.presentation.finance.viewmodel.FinanceSharedViewModel
import com.ssafy.locket.presentation.finance.viewmodel.PaymentHistoryState
import com.ssafy.locket.presentation.finance.viewmodel.PaymentHistoryViewModel
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import kotlinx.coroutines.launch

private const val TAG = "PaymentListFragment"
class PaymentListFragment : BaseFragment<FragmentPaymentListBinding>(
    FragmentPaymentListBinding::bind,
    R.layout.fragment_payment_list
) {
    private lateinit var paymentRVAdapter: PaymentRVAdapter
    private val paymentHistoryViewModel: PaymentHistoryViewModel by activityViewModels()
    private val financeSharedViewModel: FinanceSharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initUI()

        viewLifecycleOwner.lifecycleScope.launch {
            financeSharedViewModel.selectedYearMonth.collect {
                paymentHistoryViewModel.getMonthlyPaymentHistory(it.year, it.monthValue)
            }
        }
    }

    private fun initAdapter() {
        paymentRVAdapter = PaymentRVAdapter("payment")

        binding.rvPayment.apply {
            adapter = paymentRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                paymentHistoryViewModel.monthlyPaymentHistory.collect { uiState ->
                    when(uiState) {
                        is PaymentHistoryState.Success -> {
                            paymentRVAdapter.submitList(uiState.paymentMonthlyHistory.list)
                        }
                        is PaymentHistoryState.Error -> {
                            Log.d(TAG, "initUI: ${uiState.message}")
                            CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, uiState.message)
                        }
                        else -> Log.d(TAG, "initUI: else")
                    }
                }
            }
        }
    }
}