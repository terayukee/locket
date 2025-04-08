package com.ssafy.locket.presentation.finance.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.locket.presentation.common.view.MainActivity
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.common.viewmodel.FinanceNavigationState
import com.ssafy.locket.presentation.common.viewmodel.MainViewModel
import com.ssafy.locket.presentation.databinding.FragmentFinanceBinding
import com.ssafy.locket.presentation.finance.adapter.FinanceVPAdapter
import com.ssafy.locket.presentation.finance.viewmodel.BudgetViewModel
import com.ssafy.locket.presentation.finance.viewmodel.FinanceSharedViewModel
import com.ssafy.locket.presentation.finance.viewmodel.TotalPaymentState
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

private const val TAG = "FinanceFragment"

class FinanceFragment : BaseFragment<FragmentFinanceBinding>(
    FragmentFinanceBinding::bind,
    R.layout.fragment_finance
) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val financeSharedViewModel: FinanceSharedViewModel by activityViewModels()
    private val budgetViewModel: BudgetViewModel by activityViewModels()

    //뒤로 가기 이벤트
    private var backPressedTime: Long = 0
    private val today = LocalDate.now()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTabLayout()
        financeSharedViewModel.initYearMonth()

        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
                financeSharedViewModel.selectedYearMonth.collect {
                    if (it >= YearMonth.of(today.year, today.monthValue)) {
                        binding.btnNextMonthIcon.isEnabled = false
                    } else if (it < YearMonth.of(2020, 2)) {
                        binding.btnPrevMonthIcon.isEnabled = false
                    } else {
                        binding.btnPrevMonthIcon.isEnabled = true
                        binding.btnNextMonthIcon.isEnabled = true
                    }
                    binding.tvYearMonth.text =
                        resources.getString(R.string.finance_year_month, it.year, it.monthValue)
                }
//            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
                financeSharedViewModel.selectedYearMonthTotalPayment.collect { uiState ->
                    when (uiState) {
                        is TotalPaymentState.Success -> {
                            binding.tvPaymentData.text = resources.getString(
                                R.string.finance_won,
                                CommonUtils.makeCommaDecimal(uiState.totalPayment)
                            )
                        }

                        is TotalPaymentState.Error -> {
                            Log.d(TAG, "initUI: Error payment ${uiState.message}")
                            CommonUtils.showSingleLineCustomToast(
                                requireContext(),
                                ToastType.ERROR,
                                uiState.message
                            )
                        }

                        else -> Log.d(TAG, "initUI: Payment Initial or Loading")
                    }
//                }
            }
        }

        binding.btnAnalysis.setOnClickListener {
            findNavController().navigate(R.id.action_financeFragment_to_expenseAnalysisFragment)
        }

        binding.btnPrevMonthIcon.setOnClickListener {
            financeSharedViewModel.setYearMonth(
                financeSharedViewModel.selectedYearMonth.value.minusMonths(
                    1
                )
            )
            val year = financeSharedViewModel.selectedYearMonth.value.year
            val month = financeSharedViewModel.selectedYearMonth.value.monthValue
            budgetViewModel.getBudgetStatus(year, month)
        }

        binding.btnNextMonthIcon.setOnClickListener {
            Log.d(TAG, financeSharedViewModel.selectedYearMonth.toString())
            financeSharedViewModel.setYearMonth(
                financeSharedViewModel.selectedYearMonth.value.plusMonths(
                    1
                )
            )
            val year = financeSharedViewModel.selectedYearMonth.value.year
            val month = financeSharedViewModel.selectedYearMonth.value.monthValue
            budgetViewModel.getBudgetStatus(year, month)
        }
        backEvent()
    }

    private fun initTabLayout() {
        binding.tabLayout.apply {
            addTab(binding.tabLayout.newTab().setText("내역"))
            addTab(binding.tabLayout.newTab().setText("달력"))
            addTab(binding.tabLayout.newTab().setText("예산"))
        }

        binding.tabVp.apply {
            adapter = FinanceVPAdapter(requireActivity() as MainActivity)
            isUserInputEnabled = false
        }

        TabLayoutMediator(binding.tabLayout, binding.tabVp) { tab, position ->
            tab.text = if (position == 0) "내역" else if (position == 1) "달력" else "예산"
        }.attach()

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.selectedFinanceTab.collect { state ->
                Log.d(TAG, "initTabLayout: $state")
                if (state is FinanceNavigationState.Budget) {
                    binding.tabVp.setCurrentItem(2, false)
                    binding.tabLayout.getTabAt(2)?.select()
                } else {
                    binding.tabVp.setCurrentItem(0, false)
                    binding.tabLayout.getTabAt(0)?.select()
                }
            }
        }
    }

    fun backEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (System.currentTimeMillis() - backPressedTime < 2000) {
                        requireActivity().finish() // 액티비티 종료
                    } else {
                        backPressedTime = System.currentTimeMillis()
                        CommonUtils.showSingleLineCustomToast(
                            requireContext(),
                            ToastType.DEFAULT,
                            "한 번 더 누르면 종료됩니다."
                        )
                    }
                }
            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        financeSharedViewModel.initYearMonth()
    }
}