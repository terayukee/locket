package com.ssafy.locket.ui.finance.fragments.budget

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentBudgetBinding

class BudgetFragment : BaseFragment<FragmentBudgetBinding>(
    FragmentBudgetBinding::bind,
    R.layout.fragment_budget
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}