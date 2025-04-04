package com.ssafy.locket.presentation.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.presentation.common.view.MainActivity
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.common.viewmodel.FinanceNavigationState
import com.ssafy.locket.presentation.common.viewmodel.MainViewModel
import com.ssafy.locket.presentation.databinding.FragmentHomeBinding
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterInfoState
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterViewModel
import com.ssafy.locket.presentation.home.character.viewmodel.NavigationEvent
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.ViewComponentManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private const val TAG = "HomeFragment"
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {
    private val mainViewModel: MainViewModel by activityViewModels()

    private val userInfoViewModel: UserInfoViewModel by activityViewModels()

    private val characterViewModel: CharacterViewModel by activityViewModels()

    private var backPressedTime: Long = 0
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    private fun initUI() {
        val today = LocalDate.now()

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
        binding.tvBudgetAiFeedback.text = "목표 소비 금액 70% 달성 \uD83C\uDFAF"

        binding.tvPaymentData.text = getString(R.string.finance_won, CommonUtils.makeComma(200000))
        binding.tvPaymentFeedback.text = "지난 달보다 109만원 덜 썼어요"

        binding.tvBudgetTitle.text = getString(R.string.home_budget_month, today.monthValue)
        binding.tvBudgetData.text = getString(R.string.finance_won, CommonUtils.makeComma(200000))

        binding.tvBudgetFeedback.text = "100,000원 남았어요"

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
                characterViewModel.navigationEvent.collect { uiState ->
                    if(uiState is NavigationEvent.MoveToFragment) {
                        findNavController().navigate(R.id.action_homeFragment_to_characterGrowthFragment)
                    } else if(uiState is NavigationEvent.MoveToInitial) {
                        findNavController().navigate(R.id.action_homeFragment_to_characterInitialFragment)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //(requireContext() as MainActivity).changeBackgroundColor(R.color.white)
    }

    fun getActivityContext(context: Context): Context {
        return if (context is ViewComponentManager.FragmentContextWrapper) {
            context.baseContext
        } else {
            context
        }
    }


    fun initEvent(){
        lifecycleScope.launch {
            userDataStoreSource.userId.collect { id ->
                userInfoViewModel.fetchUser(id?:0)
            }
        }
    }

    fun observeModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userInfoViewModel.userInfo.collect { user ->
                    if(user is UserInfoState.Success) {
                        userDataStoreSource.saveUser(user.userInfo)
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