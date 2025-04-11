package com.ssafy.locket.presentation.finance.fragment.budget

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.common.viewmodel.FinanceNavigationState
import com.ssafy.locket.presentation.common.viewmodel.MainViewModel
import com.ssafy.locket.presentation.databinding.FragmentBudgetBinding
import com.ssafy.locket.presentation.finance.viewmodel.BudgetViewModel
import com.ssafy.locket.presentation.finance.viewmodel.FinanceSharedViewModel
import com.ssafy.locket.presentation.finance.viewmodel.GetBudgetStatusState
import com.ssafy.locket.presentation.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

private const val TAG = "BudgetFragment"
@AndroidEntryPoint
class BudgetFragment : BaseFragment<FragmentBudgetBinding>(
    FragmentBudgetBinding::bind,
    R.layout.fragment_budget
) {
    private val mainViewModel : MainViewModel by activityViewModels()
    private val financeSharedViewModel: FinanceSharedViewModel by activityViewModels()
    private val budgetViewModel : BudgetViewModel by activityViewModels()

    private val today = LocalDate.now()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                financeSharedViewModel.selectedYearMonth.collectLatest {
                    binding.btnBudgetSet.isEnabled = it.year == today.year && it.monthValue == today.monthValue
                }
            }
        }

        binding.btnBudgetSet.setOnClickListener {
            findNavController().navigate(R.id.action_financeFragment_to_editBudgetFragment)
        }
        observeViewModel()
        initEvent()
    }

    private fun getDaysInCurrentMonth(year:Int, month: Int): Int {
        return YearMonth.of(year, month).lengthOfMonth()
    }

    fun initEvent(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                financeSharedViewModel.selectedYearMonth.collectLatest {
                    budgetViewModel.getBudgetStatus(it.year, it.monthValue)
                }
            }
        }
    }

    fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                budgetViewModel.getBudgetStatus.collectLatest { response->
                    if(response is GetBudgetStatusState.Success) {
                        val today = LocalDate.now()
                        val budget = response.budgetStatus.budget.monthly
                        if(response.budgetStatus.hasBudget) {
                            val daysInSelectedMonth = getDaysInCurrentMonth(budget.year, budget.month) + 1

                            binding.groupBudget.visibility = View.VISIBLE
                            binding.tvBudgetLeftNo.visibility = View.GONE
                            if(budget.target >= budget.spent) { // 예산 남거나 다 씀
                                val budgetLeftDaily = when (budget.month) {
                                    2 -> budget.remaining/(daysInSelectedMonth-today.dayOfMonth)
                                    1, 3, 5, 7, 8, 10, 12 -> budget.remaining/(daysInSelectedMonth-today.dayOfMonth)
                                    else -> budget.remaining/(daysInSelectedMonth-today.dayOfMonth)
                                }

                                binding.tvBudgetLeft.text = getString(R.string.finance_budget_left, CommonUtils.makeComma(budget.remaining))
                                binding.tvBudgetLeftDaily.text = getString(R.string.finance_budget_left_daily, CommonUtils.makeComma(budgetLeftDaily))
                                binding.tvBudgetLeft.setTextColor(resources.getColor(R.color.text))
                            } else { // 예산보다 많이 사용함
                                binding.tvBudgetLeft.text = getString(R.string.finance_budget_more, CommonUtils.makeComma(budget.spent - budget.target))
                                binding.tvBudgetLeftDaily.text = getString(R.string.finance_budget_left_daily, "0")
                                binding.tvBudgetLeft.setTextColor(resources.getColor(R.color.finance_budget_over_text))
                            }

                            binding.tvBudget.text = getString(R.string.finance_won, CommonUtils.makeComma(budget.target))
                            binding.progressBar.setProgress(budget.progress.toInt())
                            binding.tvRecommendBudgetToday.text = getString(R.string.finance_won, CommonUtils.makeComma((budget.target/daysInSelectedMonth)*today.dayOfMonth))
                        } else {
                            if(budget.year == today.year && budget.month == today.monthValue) {
                                binding.tvBudgetLeftNo.text = getString(R.string.finance_budget_no_set_title)
                            } else {
                                binding.tvBudgetLeftNo.text = getString(R.string.finance_budget_no_set_title_prev)
                            }
                            binding.tvBudgetLeftNo.visibility = View.VISIBLE
                            binding.groupBudget.visibility = View.INVISIBLE
                            binding.tvBudgetLeft.text = getString(R.string.finance_budget_left, "0")
                            binding.tvBudgetLeftDaily.text = getString(R.string.finance_budget_left_daily, "0")
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.setSelectedFinanceTab(FinanceNavigationState.Default)
//        Log.d(TAG, "onStop: setDefault")
    }
}