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
import com.ssafy.locket.presentation.utils.ToastType

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
                if(raw.toInt()>0){
                    viewModel.updatePrice(raw)
                    binding.tvWantPrice.setText("")
                    dismiss()
                }
                else{
                    binding.tvWantPrice.setText("")
                    CommonUtils.showMultiLineCustomToast(requireContext(), "가격 설정","1원이상의 가격을 입력해주세요")
                }
            }
        }
        binding.btnClear.setOnClickListener {
            viewModel.updatePrice("")
            binding.tvWantPrice.setText("")
            dismiss()
        }
    }


    private fun setupNumberFormatting() {
        binding.tvWantPrice.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private var isFormatting = false
            private var selection = 0
            private var isDeleting = false
            private var beforeLength = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeLength = s?.length ?: 0
                selection = binding.tvWantPrice.selectionStart
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
                    val cursorPosition = binding.tvWantPrice.selectionStart

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
                        binding.tvWantPrice.setText("")
                        current = ""
                        isFormatting = false
                        return
                    }

                    // 포맷팅된 문자열 생성
                    val formattedString = CommonUtils.formatNumber(cleanString)

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
                    binding.tvWantPrice.setText(formattedString)
                    binding.tvWantPrice.setSelection(minOf(newCursorPosition, formattedString.length))

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
