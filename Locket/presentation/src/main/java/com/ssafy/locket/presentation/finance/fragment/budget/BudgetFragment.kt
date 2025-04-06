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
import com.ssafy.locket.presentation.finance.viewmodel.GetFeedbackState
import com.ssafy.locket.presentation.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
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

        var progress = 210
        binding.progressBar.setProgress(progress)

//        binding.tvBudgetLeftNo.visibility = View.VISIBLE // 예산설정 안 된 경우
        binding.groupBudget.visibility = View.VISIBLE // 예산설정 한 경우
        /*
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
        */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                financeSharedViewModel.selectedYearMonth.collect {
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

    private fun getDaysInCurrentMonth(): Int {
        return YearMonth.of(today.year, today.month).lengthOfMonth()
    }

    fun initEvent(){
        
        //나중에 날짜 year,month 나오면 여기에 넣으면 바로 데이터 들어옴
        budgetViewModel.getBudgetStatus(2025,4)
    }

    fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                budgetViewModel.getBudgetStatus.collect { reponse->
                    if(reponse is GetBudgetStatusState.Success) {
                        Log.d(TAG,reponse.budgetStatus.toString())
                        val today = LocalDate.now()
                        val currentMonth = today.monthValue
                        val budget = reponse.budgetStatus.budget.monthly.remaining

                        binding.tvBudgetLeft.text = CommonUtils.makeComma(reponse.budgetStatus.budget.monthly.remaining)+"원 남음"// budget 대신 남은 예산으로 변경
                        val budgetLeftDaily = when (currentMonth) {
                            2 -> budget/(29-today.dayOfMonth)
                            1, 3, 5, 7, 8, 10, 12 -> budget/(32-today.dayOfMonth)
                            else -> budget/(31-today.dayOfMonth)
                        }
                        binding.tvBudgetLeftDaily.text = "하루 예산 "+ CommonUtils.makeComma(budgetLeftDaily)+"원"
                        Log.d(TAG,"출력"+reponse.budgetStatus.budget.monthly.progress.toString())
                        binding.progressBar.setProgress(reponse.budgetStatus.budget.monthly.progress.toInt())
                        binding.tvBudget.text = CommonUtils.makeComma(budget)+"원 남음"
                        binding.tvRecommendBudgetToday.text = CommonUtils.makeComma((budget/getDaysInCurrentMonth())*today.dayOfMonth)+"원"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.setSelectedFinanceTab(FinanceNavigationState.Default)
    }
}