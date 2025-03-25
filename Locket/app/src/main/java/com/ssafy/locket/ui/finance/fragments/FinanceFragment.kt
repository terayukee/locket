package com.ssafy.locket.ui.finance.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentFinanceBinding
import com.ssafy.locket.ui.finance.adapter.FinanceVPAdapter
import com.ssafy.locket.ui.main.MainActivity
import java.time.YearMonth

class FinanceFragment : BaseFragment<FragmentFinanceBinding>(
    FragmentFinanceBinding::bind,
    R.layout.fragment_finance
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        arguments?.getInt("SELECTED_TAB")?.let { tabIndex ->
            binding.tabVp.setCurrentItem(tabIndex, false)
            binding.tabLayout.getTabAt(tabIndex)?.select()
        }

        var currentMonth = YearMonth.now()

        binding.tvYearMonth.text = resources.getString(R.string.finance_year_month, currentMonth.year, currentMonth.monthValue)

        binding.btnAnalysis.setOnClickListener {
            findNavController().navigate(R.id.action_financeFragment_to_expenseAnalysisFragment)
        }
    }
}