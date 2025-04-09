package com.ssafy.locket.presentation.finance.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.YearMonth

private const val TAG = "FinanceFragment"

class FinanceFragment : BaseFragment<FragmentFinanceBinding>(
    FragmentFinanceBinding::bind,
    R.layout.fragment_finance
) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val financeSharedViewModel: FinanceSharedViewModel by activityViewModels()
    private val budgetViewModel: BudgetViewModel by activityViewModels()

    private var backPressedTime: Long = 0
    private val today = YearMonth.now()
    private val fonts = arrayOf(R.font.pretendard_regular, R.font.pretendard_bold)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)?.select()

        initTabLayout()
        financeSharedViewModel.initYearMonthPayment()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                financeSharedViewModel.selectedYearMonth.collectLatest {
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
                    if(today == it) binding.icMoveToToday.visibility = View.GONE
                    else binding.icMoveToToday.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
                financeSharedViewModel.selectedYearMonthTotalPayment.collectLatest { uiState ->
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

        binding.icMoveToToday.setOnClickListener {
            financeSharedViewModel.setYearMonth(today)
            binding.btnNextMonthIcon.isEnabled = false
            binding.btnPrevMonthIcon.isEnabled = true
        }

        binding.btnAnalysis.setOnClickListener {
            findNavController().navigate(R.id.action_financeFragment_to_expenseAnalysisFragment)
        }

        binding.btnPrevMonthIcon.setOnClickListener {
            val currentYearMonth = financeSharedViewModel.selectedYearMonth.value
            val newYearMonth = currentYearMonth.minusMonths(1)
            financeSharedViewModel.setYearMonth(
                newYearMonth
            )
            budgetViewModel.getBudgetStatus(newYearMonth.year, newYearMonth.monthValue)
        }

        binding.btnNextMonthIcon.setOnClickListener {
            val currentYearMonth = financeSharedViewModel.selectedYearMonth.value
            val newYearMonth = currentYearMonth.plusMonths(1)
            financeSharedViewModel.setYearMonth(
                newYearMonth
            )
            budgetViewModel.getBudgetStatus(newYearMonth.year, newYearMonth.monthValue)
        }
        backEvent()
    }

    private fun initTabLayout() {
//        binding.tabLayout.apply {
//            addTab(binding.tabLayout.newTab().setText("내역"))
//            addTab(binding.tabLayout.newTab().setText("달력"))
//            addTab(binding.tabLayout.newTab().setText("예산"))
//        }

        binding.tabVp.apply {
            adapter = FinanceVPAdapter(requireActivity() as MainActivity)
            isUserInputEnabled = false
        }

//        binding.tabLayout.post {
//            // 0번째 탭이 선택된 상태에서 텍스트 스타일 적용
//            val tabTextView = getTextViewFromTab(binding.tabLayout.getTabAt(0)!!)
//            tabTextView?.typeface = ResourcesCompat.getFont(requireContext(), fonts[1])
//        }

        TabLayoutMediator(binding.tabLayout, binding.tabVp) { tab, position ->
            tab.text = if (position == 0) "내역" else if (position == 1) "달력" else "예산"
        }.attach()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.selectedFinanceTab.collectLatest { state ->
                    binding.tabVp.post {
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
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabTextView = getTextViewFromTab(tab)
                tabTextView?.typeface = ResourcesCompat.getFont(requireContext(), fonts[1])
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabTextView = getTextViewFromTab(tab)
                tabTextView?.typeface = ResourcesCompat.getFont(requireContext(), fonts[0])
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                val tabTextView = getTextViewFromTab(tab)
                tabTextView?.typeface = ResourcesCompat.getFont(requireContext(), fonts[1])
            }
        })
    }

    private fun getTextViewFromTab(tab: TabLayout.Tab): TextView? {
        val tabLayout = tab.parent as TabLayout
        val tabStrip = tabLayout.getChildAt(0) as ViewGroup
        val tabView = tabStrip.getChildAt(tab.position) as ViewGroup

        for (i in 0 until tabView.childCount) {
            val child = tabView.getChildAt(i)
            if (child is TextView) {
                return child
            }
        }
        return null
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

    override fun onPause() {
        super.onPause()
        financeSharedViewModel.initYearMonth()
    }
}