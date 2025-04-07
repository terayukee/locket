package com.ssafy.locket.presentation.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendar.core.yearMonth
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.presentation.common.view.MainActivity
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.common.viewmodel.FinanceNavigationState
import com.ssafy.locket.presentation.common.viewmodel.MainViewModel
import com.ssafy.locket.presentation.databinding.FragmentHomeBinding
import com.ssafy.locket.presentation.finance.viewmodel.BudgetViewModel
import com.ssafy.locket.presentation.finance.viewmodel.GetBudgetStatusState
import com.ssafy.locket.presentation.finance.viewmodel.GetShortFeedbackState
import com.ssafy.locket.presentation.finance.viewmodel.TotalPaymentState
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterInfoState
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterViewModel
import com.ssafy.locket.presentation.home.character.viewmodel.NavigationEvent
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.ViewComponentManager
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.absoluteValue

private const val TAG = "HomeFragment"
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {
    private val mainViewModel: MainViewModel by activityViewModels()

    private val userInfoViewModel: UserInfoViewModel by activityViewModels()

    private val characterViewModel: CharacterViewModel by activityViewModels()

    private val budgetViewModel : BudgetViewModel by activityViewModels()

    private val homeFinanceViewModel: HomeFinanceViewModel by viewModels()

    private var backPressedTime: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

        (getActivityContext(requireContext()) as MainActivity).changeBackgroundColor(R.color.background)

    }

    private fun initUI() {
        val today = LocalDate.now()
        homeFinanceViewModel.getMonthTotal(today.yearMonth)
        budgetViewModel.getBudgetStatus(today.year, today.monthValue)
        budgetViewModel.getShortFeedback()

        binding.ivCharacterBg.setOnClickListener {
            characterViewModel.checkCharacter()
        }

        binding.layoutReceipt.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_receiptListFragment)
        }

        binding.icNotification.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }

        binding.ivBudgetCardBg.setOnClickListener {
            mainViewModel.setSelectedFinanceTab(FinanceNavigationState.Budget)
            (getActivityContext(requireContext()) as MainActivity).setBottomNavigationIndex(R.id.household_account_book)
        }

        binding.ivPaymentCardBg.setOnClickListener {
            mainViewModel.setSelectedFinanceTab(FinanceNavigationState.Default)
            (getActivityContext(requireContext()) as MainActivity).setBottomNavigationIndex(R.id.household_account_book)
        }

        binding.btnAnalysis.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_expenseAnalysisFragment)
        }

        binding.tvPaymentTitle.text = getString(R.string.home_payment_month, today.monthValue)

        binding.tvPaymentData.text = getString(R.string.finance_won, CommonUtils.makeComma(200000))

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeFinanceViewModel.prevMonthTotal.collect { uiState ->
                    if(uiState is TotalPaymentState.Success) {
                        if((uiState.totalPayment) < BigDecimal.ZERO) {  // 이전 달에 돈을 더 적게 씀
                            binding.tvPaymentFeedback.text = getString(R.string.finance_home_payment_feedback_more, CommonUtils.makeCommaDecimal((uiState.totalPayment).abs()))
                        } else if((uiState.totalPayment) > BigDecimal.ZERO) {
                            binding.tvPaymentFeedback.text = getString(R.string.finance_home_payment_feedback_less, CommonUtils.makeCommaDecimal(uiState.totalPayment))
                        } else {
                            binding.tvPaymentFeedback.text = getString(R.string.finance_home_payment_feedback_same)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeFinanceViewModel.monthTotal.collect { uiState ->
                    when(uiState){
                        is TotalPaymentState.Success -> {
                            binding.tvPaymentData.text = getString(
                                R.string.finance_won,
                                CommonUtils.makeCommaDecimal(uiState.totalPayment)
                            )
                        }
                        is TotalPaymentState.Error -> {
                            CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, uiState.message)
                        }
                        else -> {
                            Log.d(TAG, "initUI: else loading")
                        }
                    }
                }
            }
        }
        
        binding.tvBudgetTitle.text = getString(R.string.home_budget_month, today.monthValue)

        binding.icProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
        }

        observeModel()
        initEvent()
        backEvent()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userInfoViewModel.userInfo.collect { user ->
                    if(user is UserInfoState.Success) {
                        binding.tvUserName.text = getString(R.string.home_name, user.userInfo.nickname)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                budgetViewModel.getBudgetStatus.collect { uiState ->
                    if (uiState is GetBudgetStatusState.Success) {
                        if(uiState.budgetStatus.hasBudget == false) {
                            binding.tvNoBudgetTitle.visibility = View.VISIBLE
                            binding.tvNoBudgetTitle.text = getString(R.string.home_no_budget_month)
                        } else {
                            binding.tvBudgetData.visibility = View.VISIBLE
                            binding.tvBudgetData.text = getString(R.string.finance_won, CommonUtils.makeComma(uiState.budgetStatus.budget.monthly.target))
                            val remain = uiState.budgetStatus.budget.monthly.target - uiState.budgetStatus.budget.monthly.spent
                            if(remain > 0) {
                                binding.tvBudgetFeedback.text = getString(R.string.finance_home_budget_feedback_less, CommonUtils.makeComma(remain))
                            } else if(remain < 0) {
                                binding.tvBudgetFeedback.text = getString(R.string.finance_home_budget_feedback_more, CommonUtils.makeComma(remain.absoluteValue.floorDiv(10000)))
                            } else {
                                binding.tvBudgetFeedback.text = getString(R.string.finance_home_budget_feedback_same)
                            }
                        }
                    } else if(uiState is GetBudgetStatusState.Error) {
                        Log.d(TAG, "initUI: GetBudgetStatusState error ${uiState.message}")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                characterViewModel.navigationEvent.collect { uiState ->
                    when(uiState) {
                        is NavigationEvent.MoveToFragment -> {
                            findNavController().navigate(R.id.action_homeFragment_to_characterGrowthFragment)
                        }
                        is NavigationEvent.MoveToInitial -> {
                            findNavController().navigate(R.id.action_homeFragment_to_characterInitialFragment)
                        }
                        is NavigationEvent.Error -> {
                            CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, uiState.message)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (getActivityContext(requireContext()) as MainActivity).changeBackgroundColor(R.color.white)
    }

    fun getActivityContext(context: Context): Context {
        return if (context is ViewComponentManager.FragmentContextWrapper) {
            context.baseContext
        } else {
            context
        }
    }

    fun initEvent(){
        userInfoViewModel.fetchUser()
    }

    fun observeModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userInfoViewModel.userInfo.collect { user ->
                    if(user is UserInfoState.Success) {
                        binding.tvUserName.text = getString(R.string.home_name, user.userInfo.nickname)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                budgetViewModel.getShortFeedback.collect { getShortFeedback ->
                    if(getShortFeedback is GetShortFeedbackState.Success) {
                        binding.tvBudgetAiFeedback.text = getShortFeedback.shortFeedback.feedback
                    }
                }
            }
        }
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