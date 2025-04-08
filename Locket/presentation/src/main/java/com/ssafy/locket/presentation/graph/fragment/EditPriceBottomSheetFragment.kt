package com.ssafy.locket.presentation.graph.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.FragmentEditPriceBottomSheetBinding
import com.ssafy.locket.presentation.graph.viewmodel.EditPriceViewModel
import com.ssafy.locket.presentation.utils.CommonUtils

private const val TAG = "EditPriceBottomSheetFra"
class EditPriceDialogFragment : DialogFragment() {
    private var _binding: FragmentEditPriceBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var isFormatting = false
    private val viewModel: EditPriceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPriceBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
        setupNumberFormatting()

        binding.root.setOnTouchListener { v, event ->
            binding.tvWantPrice.clearFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            false
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return

        // 원하는 폭 설정 (예: 화면 너비의 90%)
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        window.setLayout(width, height)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding.tvWantPrice.setText("")
    }

    private fun initView() {
        if (viewModel.editprice.value == "") {
            binding.tvWantPrice.hint = "설정 안됨"
            binding.btnClear.isEnabled = false
            binding.btnClear.setBackgroundResource(R.drawable.bg_bottom_btn_clear)
        } else {
            binding.tvWantPrice.hint = "현재 가격은 ${CommonUtils.formatNumber(viewModel.editprice.value)} 원 입니다"
        }
    }

    private fun initEvent() {
        binding.btnConfirm.setOnClickListener {
            val input = binding.tvWantPrice.text.toString()
            if (input.isNotEmpty()) {
                val raw = input.replace(",", "")
                viewModel.updatePrice(raw)
                binding.tvWantPrice.setText("")
            }
            dismiss()
        }
        binding.btnClear.setOnClickListener {
            viewModel.updatePrice("")
            binding.tvWantPrice.setText("")
            dismiss()
        }
    }

    private fun setupNumberFormatting() {
        binding.tvWantPrice.addTextChangedListener(object : TextWatcher {
            private var beforeText = ""
            private var cursorPosition = 0
            private var isDeleting = false
            private var deletedChar = ""
            private var deleteIndex = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeText = s.toString()
                cursorPosition = binding.tvWantPrice.selectionStart
                isDeleting = count > after
                if (isDeleting && s != null && count == 1) {
                    deletedChar = s.substring(start, start + count)
                    deleteIndex = start
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                var digits = s.toString().replace(",", "")
                if (digits.isEmpty()) return

                if (isDeleting && deletedChar == "," && deleteIndex > 0) {
                    val digitsBeforeCursor = beforeText.replace(",", "")
                    val indexToRemove = deleteIndex - beforeText.take(deleteIndex).count { it == ',' }
                    if (indexToRemove > 0 && indexToRemove <= digits.length) {
                        digits = digits.removeRange(indexToRemove - 1, indexToRemove)
                    }
                }

                isFormatting = true
                val formatted = CommonUtils.formatNumber(digits)
                binding.tvWantPrice.setText(formatted)

                val commaCountBefore = beforeText.take(cursorPosition).count { it == ',' }
                val commaCountNow = formatted.take(cursorPosition).count { it == ',' }
                val adjustment = commaCountNow - commaCountBefore
                val newCursor = (cursorPosition + adjustment).coerceIn(0, formatted.length)

                try {
                    binding.tvWantPrice.setSelection(newCursor)
                } catch (e: Exception) {
                    binding.tvWantPrice.setSelection(formatted.length)
                }

                isFormatting = false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "EditPriceDialogFragment"
        fun newInstance(): EditPriceDialogFragment {
            return EditPriceDialogFragment()
        }
    }
}
