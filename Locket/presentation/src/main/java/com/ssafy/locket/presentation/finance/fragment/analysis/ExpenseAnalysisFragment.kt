package com.ssafy.locket.presentation.finance.fragment.analysis

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import com.ssafy.locket.presentation.finance.viewmodel.BudgetViewModel
import com.ssafy.locket.presentation.finance.viewmodel.GetFeedbackState
import com.ssafy.locket.presentation.utils.CommonUtils
import kotlinx.coroutines.launch
import java.time.YearMonth

private const val TAG = "ExpenseAnalysisFragment"
class ExpenseAnalysisFragment : BaseFragment<FragmentExpenseAnalysisBinding>(
    FragmentExpenseAnalysisBinding::bind,
    R.layout.fragment_expense_analysis
) {
    private lateinit var categoryPaymentRVAdapter: CategoryPaymentRVAdapter

    private val analysisViewModel : AnalysisViewModel by activityViewModels()

    private var currentMonth = YearMonth.now()
    private val startMonth = YearMonth.of(2025, 1)
    private val endMonth = YearMonth.of(2025, 5)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initEvent()
        getFeedbackData()
        Log.d(TAG,"시작")

        //연습용 ai 기능되면 데이터 가져오는 거 됨
        analysisViewModel.getFeedback(2025,3)
    }

    fun initEvent(){
        binding.tvCategoryType.text = "카페인 뱀파이어형"
        binding.tvFeedbackContent.text = getString(R.string.finance_analysis_feedback_test)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvPaymentData.text = getString(R.string.finance_won, CommonUtils.makeComma(100000))
        binding.tvYearMonth.text = getString(R.string.finance_year_month, currentMonth.year, currentMonth.monthValue)
        binding.btnPrevMonthIcon.setOnClickListener {
            updateTitle(currentMonth.minusMonths(1))
        }

        binding.btnNextMonthIcon.setOnClickListener {
            updateTitle(currentMonth.plusMonths(1))
        }
    }


    private fun updateTitle(month: YearMonth) {
        currentMonth = month

        binding.tvYearMonth.text = getString(R.string.finance_year_month, month.year, month.monthValue)
        binding.btnPrevMonthIcon.isEnabled = month > startMonth
        binding.btnNextMonthIcon.isEnabled = month < endMonth
    }

    private fun initAdapter() {
        categoryPaymentRVAdapter = CategoryPaymentRVAdapter()

        binding.rvCategoryPayment.apply {
            adapter = categoryPaymentRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val tmpList : List<CategoryPayment> = listOf(CategoryPayment("shopping",45.2f, 3000), CategoryPayment("cafe",22.2f, 85000))
        categoryPaymentRVAdapter.submitList(tmpList)
    }

    fun getFeedbackData(){
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