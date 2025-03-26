package com.ssafy.locket.presentation.finance.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.locket.presentation.finance.fragments.budget.BudgetFragment
import com.ssafy.locket.presentation.finance.fragments.payment_calendar.PaymentCalendarFragment
import com.ssafy.locket.presentation.finance.fragments.payment_list.PaymentListFragment

class FinanceVPAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){

    private val fragments = listOf<Fragment>(
        PaymentListFragment(),
        PaymentCalendarFragment(),
        BudgetFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}