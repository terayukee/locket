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
import com.ssafy.locket.presentation.databinding.FragmentEditBudgetBinding
import com.ssafy.locket.presentation.finance.viewmodel.BudgetViewModel
import com.ssafy.locket.presentation.finance.viewmodel.GetBudgetStatusState
import com.ssafy.locket.presentation.finance.viewmodel.SetBudgetState
import com.ssafy.locket.presentation.graph.viewmodel.ProductHappyListState
import com.ssafy.locket.presentation.utils.CommonUtils
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate

private const val TAG = "EditBudgetFragment"
class EditBudgetFragment : BaseFragment<FragmentEditBudgetBinding>(
    FragmentEditBudgetBinding::bind,
    R.layout.fragment_edit_budget
) {
    private val budgetViewModel : BudgetViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validateInputForm(binding.etGoalBudget)

        binding.tvTitle.text = getString(R.string.finance_budget_edit_title, LocalDate.now().monthValue)


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        
        binding.btnBudgetSet.setOnClickListener { 
            // TODO api 전송
            val rawNumber = binding.etGoalBudget.text.toString().replace(",", "")
            budgetViewModel.setBudgetGoal(rawNumber.toInt())
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            budgetViewModel.getBudgetStatus.collect { uiState ->
                when(uiState) {
                    is GetBudgetStatusState.Success -> {
                        binding.etGoalBudget.hint = CommonUtils.makeComma(uiState.budgetStatus.budget.monthly.target)
                    }
                    is GetBudgetStatusState.Error -> {
                        Log.e(TAG, uiState.message)
                    }
                    else -> {}
                }
            }
            if(binding.etGoalBudget.text.toString()!="") {
                val rawNumber = binding.etGoalBudget.text.toString().replace(",", "")
                budgetViewModel.setBudgetGoal(rawNumber.toInt())
            }
            findNavController().navigate(R.id.action_editBudgetFragment_to_financeFragment)
        }
    }

    private fun validateInputForm(editText: EditText) = with(binding) {
        val decimalFormat = DecimalFormat("#,###")

        editText.addTextChangedListener(object : TextWatcher {
            private var beforeText = ""
            private var cursorPosition = 0
            private var isFormatting = false
            private var isDeleting = false
            private var deletedChar = ""
            private var deleteIndex = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeText = s.toString()
                cursorPosition = editText.selectionStart
                isDeleting = count > after
                if (isDeleting && s != null && count == 1) {
                    deletedChar = s.substring(start, start + count)
                    deleteIndex = start
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                val original = s.toString()
                var cleanString = original.replace(",", "")
                if (cleanString.isEmpty()) return
                // 쉼표 바로 뒤에서 삭제한 경우 처리
                if (isDeleting && deletedChar == "," && deleteIndex > 0) {
                    val indexToRemove = deleteIndex - beforeText.take(deleteIndex).count { it == ',' }
                    if (indexToRemove > 0 && indexToRemove <= cleanString.length) {
                        cleanString = cleanString.removeRange(indexToRemove - 1, indexToRemove)
                    }
                }
                try {
                    val parsed = cleanString.toDouble()
                    val formatted = decimalFormat.format(parsed)
                    if (formatted != original) {
                        isFormatting = true
                        editText.setText(formatted)
                        // 커서 위치 보정
                        val commaCountBefore = beforeText.take(cursorPosition).count { it == ',' }
                        val commaCountNow = formatted.take(cursorPosition).count { it == ',' }
                        val adjustment = commaCountNow - commaCountBefore
                        val newCursor = (cursorPosition + adjustment).coerceIn(0, formatted.length)
                        try {
                            editText.setSelection(newCursor)
                        } catch (e: Exception) {
                            editText.setSelection(formatted.length)
                        }
                        isFormatting = false
                    }
                } catch (e: NumberFormatException) {
                    // 예외 무시
                }
            }
        })
    }
}