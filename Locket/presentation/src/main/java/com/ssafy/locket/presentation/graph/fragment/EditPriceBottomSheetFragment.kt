package com.ssafy.locket.presentation.graph.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.locket.presentation.databinding.FragmentEditPriceBottomSheetBinding
import com.ssafy.locket.presentation.graph.viewmodel.EditPriceViewModel
import com.ssafy.locket.presentation.utils.CommonUtils

private const val TAG = "EditPriceBottomSheetFra"
class EditPriceBottomSheetFragment() : BottomSheetDialogFragment() {
    private var _binding: FragmentEditPriceBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var isFormatting = false
    private val viewModel: EditPriceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPriceBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
        setupNumberFormatting()
    }

    fun initView(){
        if(viewModel.editprice.value==""){
            binding.tvWantPrice.hint = "설정 안됨"
            binding.btnClear.isEnabled = false
            binding.btnClear.setBackgroundColor(Color.parseColor("#C9C9C9"))
        }
        else {
            binding.tvWantPrice.hint = "현재 가격은 "+ CommonUtils.formatNumber(viewModel.editprice.value)+" 원 입니다"
        }
    }
    
    fun initEvent(){
        binding.btnConfirm.setOnClickListener {
            // Get the raw number (without commas)
            Log.d(TAG,binding.tvWantPrice.text.toString())
            if(binding.tvWantPrice.text.toString()!=""){
                val rawNumber = binding.tvWantPrice.text.toString().replace(",", "")
                viewModel.updatePrice(rawNumber)
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
                val original = s.toString()
                var digits = original.replace(",", "")
                if (digits.isEmpty()) return
                // 쉼표 뒤에서 삭제했는지 체크
                if (isDeleting && deletedChar == "," && deleteIndex > 0) {
                    // 쉼표 앞 숫자 하나도 제거
                    val digitsBeforeCursor = beforeText.replace(",", "")
                    val indexToRemove = deleteIndex - beforeText.take(deleteIndex).count { it == ',' }
                    if (indexToRemove > 0 && indexToRemove <= digits.length) {
                        digits = digits.removeRange(indexToRemove - 1, indexToRemove)
                    }
                }
                isFormatting = true
                val formatted = CommonUtils.formatNumber(digits)
                binding.tvWantPrice.setText(formatted)
                // 커서 위치 보정
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
        const val TAG = "EditPriceBottomSheetFragment"
        fun newInstance(): EditPriceBottomSheetFragment {
            return EditPriceBottomSheetFragment()
        }
    }
}