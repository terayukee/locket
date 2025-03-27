package com.ssafy.locket.presentation.finance.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.locket.CommonUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.locket.presentation.MainActivity
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentFinanceBinding
import com.ssafy.locket.presentation.finance.adapter.FinanceVPAdapter
import kotlinx.coroutines.launch
import java.time.YearMonth

private const val TAG = "FinanceFragment"
class FinanceFragment : BaseFragment<FragmentFinanceBinding>(
    FragmentFinanceBinding::bind,
    R.layout.fragment_finance
) {
//    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTabLayout()

        var currentMonth = YearMonth.now()

        binding.tvYearMonth.text = resources.getString(R.string.finance_year_month, currentMonth.year, currentMonth.monthValue)

        binding.tvPaymentData.text = resources.getString(R.string.finance_won, CommonUtils.makeComma(100000))
        binding.btnAnalysis.setOnClickListener {
            findNavController().navigate(R.id.action_financeFragment_to_expenseAnalysisFragment)
        }
    }

    private fun initTabLayout(){
        binding.tabLayout.apply {
            addTab(binding.tabLayout.newTab().setText("내역"))
            addTab(binding.tabLayout.newTab().setText("달력"))
            addTab(binding.tabLayout.newTab().setText("예산"))
        }

        binding.tabVp.apply {
            adapter = FinanceVPAdapter(requireActivity() as MainActivity)
            isUserInputEnabled = false
        }

        TabLayoutMediator(binding.tabLayout, binding.tabVp){ tab, position ->
            tab.text = if (position == 0) "내역" else if (position == 1) "달력" else "예산"
        }.attach()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                mainViewModel.selectedFinanceTab.collect { state ->
//                    Log.d(TAG, "initTabLayout: $state")
//                    if(state is FinanceNavigationState.Budget){
//                        binding.tabVp.setCurrentItem(2, false)
//                        binding.tabLayout.getTabAt(2)?.select()
//                    }
//                }
            }
        }

//        arguments?.getInt("SELECTED_TAB")?.let { tabIndex ->
//            binding.tabVp.setCurrentItem(tabIndex, false)
//            binding.tabLayout.getTabAt(tabIndex)?.select()
//        }
    }
}