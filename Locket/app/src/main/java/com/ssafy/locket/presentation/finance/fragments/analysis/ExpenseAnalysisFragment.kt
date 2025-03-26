package com.ssafy.locket.presentation.finance.fragments.analysis

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locket.CommonUtils
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.data.model.dto.CategoryPayment
import com.ssafy.locket.databinding.FragmentExpenseAnalysisBinding
import com.ssafy.locket.presentation.finance.adapter.CategoryPaymentRVAdapter
import java.time.YearMonth

private const val TAG = "ExpenseAnalysisFragment"
class ExpenseAnalysisFragment : BaseFragment<FragmentExpenseAnalysisBinding>(
    FragmentExpenseAnalysisBinding::bind,
    R.layout.fragment_expense_analysis
) {
    private lateinit var categoryPaymentRVAdapter: CategoryPaymentRVAdapter

    private var currentMonth = YearMonth.now()
    private val startMonth = YearMonth.of(2025, 1)
    private val endMonth = YearMonth.of(2025, 5)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        initAdapter()

        binding.tvCategoryType.text = "카페인 뱀파이어형"
        binding.tvFeedbackContent.text = getString(R.string.finance_analysis_feedback_test)

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

}