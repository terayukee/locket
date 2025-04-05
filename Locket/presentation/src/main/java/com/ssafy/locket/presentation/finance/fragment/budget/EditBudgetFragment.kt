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
import com.ssafy.locket.presentation.finance.viewmodel.SetBudgetState
import com.ssafy.locket.presentation.graph.viewmodel.ProductHappyListState
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate

private const val TAG = "EditBudgetFragment"
class EditBudgetFragment : BaseFragment<FragmentEditBudgetBinding>(
    FragmentEditBudgetBinding::bind,
    R.layout.fragment_edit_budget
) {
    private val budgetViewModel : BudgetViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validateInputForm(binding.etGoalBudget)

        binding.tvTitle.text = getString(R.string.finance_budget_edit_title, LocalDate.now().monthValue)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        
        binding.btnBudgetSet.setOnClickListener { 
            // TODO api 전송
            budgetViewModel.setBudgetGoal(binding.etGoalBudget.text.toString().toInt())
        }
    }

    private fun validateInputForm(editText: EditText) = with(binding) {
        var result = ""
        val decimalFormat = DecimalFormat("#,###")

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                if (!TextUtils.isEmpty(charSequence!!.toString()) && charSequence.toString() != result) {
                    result =
                        decimalFormat.format(charSequence.toString().replace(",", "").toDouble())
                    editText.setText(result)

                    editText.setSelection(result.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
}