package com.ssafy.locket.presentation.finance.fragment.analysis

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.finance.CategoryPayment
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentExpenseAnalysisBinding
import com.ssafy.locket.presentation.finance.adapter.CategoryPaymentRVAdapter
import com.ssafy.locket.presentation.finance.viewmodel.AnalysisViewModel
import com.ssafy.locket.presentation.finance.viewmodel.FinanceSharedViewModel
import com.ssafy.locket.presentation.finance.viewmodel.GetFeedbackState
import com.ssafy.locket.presentation.finance.viewmodel.TotalPaymentState
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import kotlinx.coroutines.launch
import java.time.YearMonth

private const val TAG = "ExpenseAnalysisFragment"
class ExpenseAnalysisFragment : BaseFragment<FragmentExpenseAnalysisBinding>(
    FragmentExpenseAnalysisBinding::bind,
    R.layout.fragment_expense_analysis
) {
    private lateinit var categoryPaymentRVAdapter: CategoryPaymentRVAdapter

    private val analysisViewModel : AnalysisViewModel by activityViewModels()
    private val financeSharedViewModel: FinanceSharedViewModel by activityViewModels()

    private var currentMonth = YearMonth.now()
    private val startMonth = YearMonth.of(2025, 1)
    private val endMonth = YearMonth.of(2025, 5)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initEvent()
        getFeedbackData()
        Log.d(TAG,"시작")
        financeSharedViewModel.initYearMonth()
    }

    fun initEvent(){
        binding.tvCategoryType.text = "카페인 뱀파이어형"
        binding.tvFeedbackContent.text = getString(R.string.finance_analysis_feedback_test)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvYearMonth.text = getString(R.string.finance_year_month, currentMonth.year, currentMonth.monthValue)
        binding.btnPrevMonthIcon.setOnClickListener {
            updateTitle(currentMonth.minusMonths(1))
        }

        binding.btnNextMonthIcon.setOnClickListener {
            updateTitle(currentMonth.plusMonths(1))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                financeSharedViewModel.selectedYearMonth.collect {
                    if(it > YearMonth.of(2029,12)) {
                        binding.btnNextMonthIcon.isEnabled = false
                    } else if (it < YearMonth.of(2020,2)) {
                        binding.btnPrevMonthIcon.isEnabled = false
                    } else {
                        binding.btnPrevMonthIcon.isEnabled = true
                        binding.btnNextMonthIcon.isEnabled = true
                    }
                    analysisViewModel.getFeedback(it.year,it.monthValue)
                    binding.tvYearMonth.text = resources.getString(R.string.finance_year_month, it.year, it.monthValue)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            financeSharedViewModel.selectedYearMonthTotalPayment.collect { uiState ->
                when(uiState) {
                    is TotalPaymentState.Success -> {
                        binding.tvPaymentData.text = resources.getString(R.string.finance_won, CommonUtils.makeCommaDecimal(uiState.totalPayment))
                    }
                    is TotalPaymentState.Error -> {
                        Log.d(TAG, "initUI: Error payment ${uiState.message}")
                        CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, uiState.message)
                    }
                    else -> Log.d(TAG, "initUI: Payment Initial or Loading")
                }
            }
        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                analysisViewModel.getFeedback.collect { uiState ->
//                    when(uiState) {
//                        is CategoryPaymentState.Success -> {
//                            categoryPaymentRVAdapter.submitList(uiState.categoryPaymentList)
//                        }
//                        is CategoryPaymentState.Error -> {
//                            Log.d(TAG, "initUI: Error payment ${uiState.message}")
//                            CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, uiState.message)
//                        }
//                        else -> Log.d(TAG, "initUI: Payment Initial or Loading")
//                    }
//                }
//            }
//        }
    }


    private fun updateTitle(month: YearMonth) {
        currentMonth = month

        binding.tvYearMonth.text = getString(R.string.finance_year_month, month.year, month.monthValue)
        binding.btnPrevMonthIcon.isEnabled = month > startMonth
        binding.btnNextMonthIcon.isEnabled = month < endMonth

        financeSharedViewModel.setYearMonth(currentMonth)
    }

    private fun initAdapter() {
        categoryPaymentRVAdapter = CategoryPaymentRVAdapter()

        binding.rvCategoryPayment.apply {
            adapter = categoryPaymentRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }


//        val tmpList : List<CategoryPayment> = listOf(CategoryPayment("shopping",45.2f, 3000), CategoryPayment("cafe",22.2f, 85000))
//        categoryPaymentRVAdapter.submitList(tmpList)
    }

    fun getFeedbackData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                analysisViewModel.getFeedback.collect { getFeedback ->
                    if(getFeedback is GetFeedbackState.Success) {
                        Log.d(TAG,getFeedback.feedback.toString())
                    }
                }
            }
        }
    }
}