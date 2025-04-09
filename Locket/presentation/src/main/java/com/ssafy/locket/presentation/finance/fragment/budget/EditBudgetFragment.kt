package com.ssafy.locket.presentation.finance.fragment.budget

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.common.viewmodel.FinanceNavigationState
import com.ssafy.locket.presentation.common.viewmodel.MainViewModel
import com.ssafy.locket.presentation.databinding.FragmentEditBudgetBinding
import com.ssafy.locket.presentation.finance.viewmodel.BudgetViewModel
import com.ssafy.locket.presentation.finance.viewmodel.GetBudgetStatusState
import com.ssafy.locket.presentation.finance.viewmodel.SetBudgetState
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate

private const val TAG = "EditBudgetFragment"
class EditBudgetFragment : BaseFragment<FragmentEditBudgetBinding>(
    FragmentEditBudgetBinding::bind,
    R.layout.fragment_edit_budget
) {
    private val budgetViewModel : BudgetViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validateInputForm(binding.etGoalBudget)

        binding.tvTitle.text = getString(R.string.finance_budget_edit_title, LocalDate.now().monthValue)


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        
        binding.btnBudgetSet.setOnClickListener {
            if(binding.etGoalBudget.text.toString()!="") {
                val rawNumber = binding.etGoalBudget.text.toString().replace(",", "")
                if(rawNumber.toInt()>0){
                    budgetViewModel.setBudgetGoal(rawNumber.toInt())
                }
                else{
                    binding.etGoalBudget.setText("")
                    CommonUtils.showMultiLineCustomToast(requireContext(), "가격 설정", "1원 이상의 가격을 입력해 주세요")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            budgetViewModel.setBudgetGoal.collect { uiState ->
                when(uiState) {
                    is SetBudgetState.Success -> {
//                        CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.DEFAULT, "예산이 ${CommonUtils.makeComma(uiState.setBudget.amount)}원으로 설정되었습니다")
                        mainViewModel.setSelectedFinanceTab(FinanceNavigationState.Budget)
                        findNavController().navigate(R.id.action_editBudgetFragment_to_financeFragment)
                    }
                    is SetBudgetState.Error -> {
                        CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, uiState.message)
                        mainViewModel.setSelectedFinanceTab(FinanceNavigationState.Budget)
                        findNavController().navigate(R.id.action_editBudgetFragment_to_financeFragment)
                    }
                    else -> {}
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            budgetViewModel.getBudgetStatus.collect { uiState ->
                when(uiState) {
                    is GetBudgetStatusState.Success -> {
                        binding.etGoalBudget.hint = CommonUtils.makeComma(uiState.budgetStatus.budget.monthly.target)
                    }
                    is GetBudgetStatusState.Error -> {
                        Log.e(TAG, uiState.message)
                        //CommonUtils.showMultiLineCustomToast(requireContext(), "가격 설정", "1원 이상의 가격을 입력해 주세요")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun validateInputForm(editText: EditText) = with(binding) {
        val decimalFormat = DecimalFormat("#,###")

        editText.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private var isFormatting = false
            private var selection = 0
            private var isDeleting = false
            private var beforeLength = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeLength = s?.length ?: 0
                selection = editText.selectionStart
                isDeleting = count > after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 필요 없음
            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val str = s.toString()

                // 빈 문자열이면 처리하지 않음
                if (str.isEmpty()) {
                    current = ""
                    isFormatting = false
                    return
                }

                try {
                    // 현재 커서 위치와 삭제 여부 확인
                    val cursorPosition = editText.selectionStart

                    // 현재 입력된 문자열에서 쉼표 제거
                    var cleanString = str.replace(",", "")

                    // 쉼표 주변에서 삭제하는 특별한 경우 처리
                    var specialDelete = false
                    var targetDigitPosition = -1

                    // 삭제 중이고 특별한 경우 처리
                    if (isDeleting && selection > 0 && beforeLength > str.length) {
                        // 커서 위치 바로 앞이 쉼표였는지 확인 (쉼표 뒤에서 지우는 상황)
                        val wasCommaBeforeCursor = selection <= current.length && selection > 0 && current[selection - 1] == ','

                        // 커서 위치 바로 뒤가 쉼표인지 확인 (쉼표 앞에서 지우는 상황)
                        val wasCommaAfterCursor = selection < current.length && current[selection] == ','

                        // 쉼표 주변에서 지우는 경우
                        if (wasCommaBeforeCursor || wasCommaAfterCursor) {
                            specialDelete = true

                            // 삭제할 위치 결정
                            val deletePosition = if (wasCommaBeforeCursor) {
                                // 쉼표 앞의 숫자 위치 (쉼표 바로 앞 숫자)
                                getCleanPosition(current, selection - 1) - 1
                            } else {
                                // 쉼표 앞의 숫자 위치 (현재 커서 위치의 숫자)
                                getCleanPosition(current, selection) - 1
                            }

                            // 삭제 후 커서가 위치해야 할 숫자 위치 저장
                            targetDigitPosition = deletePosition

                            if (deletePosition >= 0) {
                                val cleanCurrent = current.replace(",", "")
                                cleanString = StringBuilder(cleanCurrent)
                                    .deleteCharAt(deletePosition)
                                    .toString()
                            }
                        }
                    }

                    if (cleanString.isEmpty()) {
                        editText.setText("")
                        current = ""
                        isFormatting = false
                        return
                    }

                    // 포맷팅된 문자열 생성
                    val formattedString = decimalFormat.format(cleanString.toLong())

                    // 새 커서 위치 계산
                    var newCursorPosition = cursorPosition

                    // 특별한 삭제 상황인 경우 (쉼표 주변에서 삭제)
                    if (specialDelete && targetDigitPosition >= 0) {
                        // 새 포맷팅된 문자열에서 쉼표 바로 뒤로 커서 위치 설정
                        var commaCount = 0
                        var digitCount = 0
                        var i = 0

                        while (i < formattedString.length) {
                            if (formattedString[i] == ',') {
                                if (digitCount == targetDigitPosition) {
                                    // 삭제한 위치 바로 다음 쉼표를 찾았으면 그 뒤에 커서 위치
                                    newCursorPosition = i + 1
                                    break
                                }
                                commaCount++
                                i++
                            } else if (formattedString[i].isDigit()) {
                                digitCount++
                                i++
                            } else {
                                i++
                            }
                        }

                        // 적절한 쉼표를 찾지 못했거나 끝에 도달한 경우
                        if (i >= formattedString.length) {
                            newCursorPosition = formattedString.length
                        }
                    } else {
                        // 일반적인 경우 - 커서 위치까지의 숫자 개수 파악
                        val digitPosition = getCleanPosition(str, cursorPosition)

                        // 새 포맷팅된 문자열에서 해당 위치 찾기
                        newCursorPosition = findPositionOfDigit(formattedString, digitPosition)
                    }

                    // EditText 업데이트 및 커서 위치 설정
                    current = formattedString
                    editText.setText(formattedString)
                    editText.setSelection(minOf(newCursorPosition, formattedString.length))

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                isFormatting = false
            }

            // 포맷팅된 문자열에서 특정 위치까지의 숫자 개수 반환
            private fun getCleanPosition(formattedString: String, position: Int): Int {
                var count = 0
                for (i in 0 until minOf(position, formattedString.length)) {
                    if (formattedString[i].isDigit()) {
                        count++
                    }
                }
                return count
            }

            // 포맷팅된 문자열에서 n번째 숫자의 위치 찾기
            private fun findPositionOfDigit(formattedString: String, digitPosition: Int): Int {
                var count = 0
                for (i in formattedString.indices) {
                    if (formattedString[i].isDigit()) {
                        if (count == digitPosition) {
                            return i
                        }
                        count++
                    }
                }
                return formattedString.length
            }
        })
    }

    override fun onStop() {
        super.onStop()
    }
}