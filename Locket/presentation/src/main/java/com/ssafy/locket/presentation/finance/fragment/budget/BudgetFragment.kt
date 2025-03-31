package com.ssafy.locket.presentation.finance.fragment.budget

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.locket.CommonUtils
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.common.viewmodel.FinanceNavigationState
import com.ssafy.locket.presentation.common.viewmodel.MainViewModel
import com.ssafy.locket.presentation.databinding.FragmentBudgetBinding
import java.time.LocalDate
import java.time.YearMonth

class BudgetFragment : BaseFragment<FragmentBudgetBinding>(
    FragmentBudgetBinding::bind,
    R.layout.fragment_budget
) {
    private val mainViewModel : MainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var progress = 210
        binding.progressBar.setProgress(progress)

//        binding.tvBudgetLeftNo.visibility = View.VISIBLE // 예산설정 안 된 경우
        binding.groupBudget.visibility = View.VISIBLE // 예산설정 한 경우

        val budget = 340000
        val today = LocalDate.now()
        val currentMonth = today.monthValue

        binding.tvBudgetLeft.text = resources.getString(R.string.finance_budget_left, CommonUtils.makeComma(budget)) // budget 대신 남은 예산으로 변경
        val budgetLeftDaily = when (currentMonth) {
            2 -> budget/(29-today.dayOfMonth)
            1, 3, 5, 7, 8, 10, 12 -> budget/(32-today.dayOfMonth)
            else -> budget/(31-today.dayOfMonth)
        }

        binding.tvBudgetLeftDaily.text = resources.getString(R.string.finance_budget_left_daily, CommonUtils.makeComma(budgetLeftDaily))

        binding.tvBudget.text = resources.getString(R.string.finance_budget_left, CommonUtils.makeComma(budget))
        binding.tvRecommendBudgetToday.text = resources.getString(R.string.finance_won, CommonUtils.makeComma((budget/getDaysInCurrentMonth())*today.dayOfMonth))

        binding.btnBudgetSet.setOnClickListener {
            findNavController().navigate(R.id.action_financeFragment_to_editBudgetFragment)
        }
    }
    private fun getDaysInCurrentMonth(): Int {
        val currentDate = LocalDate.now()
        return YearMonth.of(currentDate.year, currentDate.month).lengthOfMonth()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.setSelectedFinanceTab(FinanceNavigationState.Default)
    }
}