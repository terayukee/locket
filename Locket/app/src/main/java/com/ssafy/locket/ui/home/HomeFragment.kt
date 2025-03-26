package com.ssafy.locket.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.locket.CommonUtils
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentHomeBinding
import com.ssafy.locket.ui.MainActivity
import java.time.LocalDate

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val today = LocalDate.now()

        binding.ivCharacterBg.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_characterFragment)
        }

        binding.layoutReceipt.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_receiptListFragment)
        }

        binding.icNotification.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }

        binding.ivBudgetCardBg.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("SELECTED_TAB", 2)
            }
            (requireContext() as MainActivity).setBottomNavigationIndex(R.id.household_account_book)
//            findNavController().navigate(R.id.action_homeFragment_to_financeFragment, bundle)
        }

        binding.ivPaymentCardBg.setOnClickListener {
            (requireContext() as MainActivity).setBottomNavigationIndex(R.id.household_account_book)
        }

        binding.btnAnalysis.setOnClickListener {
            findNavController().navigate(R.id.action_financeFragment_to_expenseAnalysisFragment)
        }

        binding.tvUserName.text = getString(R.string.home_name, "아영")
        binding.tvPaymentTitle.text = getString(R.string.home_payment_month, today.monthValue)
        binding.tvBudgetAiFeedback.text = "목표 소비 금액 70% 달성 \uD83C\uDFAF"

        binding.tvPaymentData.text = getString(R.string.finance_won, CommonUtils.makeComma(200000))
        binding.tvPaymentFeedback.text = "지난 달보다 109만원 덜 썼어요"

        binding.tvBudgetTitle.text = getString(R.string.home_budget_month, today.monthValue)
        binding.tvBudgetData.text = getString(R.string.finance_won, CommonUtils.makeComma(200000))

        binding.tvBudgetFeedback.text = "100,000원 남았어요"

        (requireContext() as MainActivity).changeBackgroundColor(R.color.background)
    }

    override fun onStop() {
        super.onStop()
        (requireContext() as MainActivity).changeBackgroundColor(R.color.white)
    }
}