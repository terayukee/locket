package com.ssafy.locket.presentation.home

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.common.view.MainActivity
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.common.viewmodel.FinanceNavigationState
import com.ssafy.locket.presentation.common.viewmodel.MainViewModel
import com.ssafy.locket.presentation.databinding.FragmentHomeBinding
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import java.time.LocalDate

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {
    private val mainViewModel: MainViewModel by activityViewModels()

    //뒤로 가기 이벤트
    private var backPressedTime: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val today = LocalDate.now()

        binding.logoLocket.setOnClickListener {
            CommonUtils.showMultiLineCustomToast(requireContext(), "유효하지 않은 입력이에요","1900 - 2025년 사이로 입력해주세요")
        }

        binding.ivCharacterBg.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_characterInitialFragment)
        }

        binding.layoutReceipt.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_receiptListFragment)
        }

        binding.icNotification.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }

        binding.ivBudgetCardBg.setOnClickListener {
            mainViewModel.setSelectedFinanceTab(FinanceNavigationState.Budget)
            (requireContext() as MainActivity).setBottomNavigationIndex(R.id.household_account_book)
        }

        binding.ivPaymentCardBg.setOnClickListener {
            mainViewModel.setSelectedFinanceTab(FinanceNavigationState.Default)
            (requireContext() as MainActivity).setBottomNavigationIndex(R.id.household_account_book)
        }

        binding.btnAnalysis.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_expenseAnalysisFragment)
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
        binding.icProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
        }

        backEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireContext() as MainActivity).changeBackgroundColor(R.color.white)
    }

    fun backEvent(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2000) {
                    requireActivity().finish() // 액티비티 종료
                } else {
                    backPressedTime = System.currentTimeMillis()
                    CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.DEFAULT, "한 번 더 누르면 종료됩니다.")
                }
            }
        })
    }
}