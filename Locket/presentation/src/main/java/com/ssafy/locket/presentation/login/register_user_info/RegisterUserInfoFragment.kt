package com.ssafy.locket.presentation.login.register_user_info

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentRegisterUserInfoBinding
import com.ssafy.locket.presentation.databinding.PopupJobMenuBinding
import com.ssafy.locket.presentation.login.LoginViewModel
import com.ssafy.locket.presentation.utils.CommonUtils

class RegisterUserInfoFragment : BaseFragment<FragmentRegisterUserInfoBinding>(
    FragmentRegisterUserInfoBinding::bind,
    R.layout.fragment_register_user_info
) {
    private var isJobSelected = false
    private var isBirthValid = false
    private val loginViewModel: LoginViewModel by activityViewModels()
    private var popupWindow: PopupWindow? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeVariable()
        initEvent()
    }

    override fun onDestroyView() {
        popupWindow?.dismiss()
        popupWindow = null
        super.onDestroyView()
    }

    private fun initializeVariable() {
        isJobSelected = false
        isBirthValid = false
    }

    private fun initEvent() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cvJobSelect.setOnClickListener {
            // 키보드 내리기
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etBirth.windowToken, 0)
            binding.etBirth.clearFocus()

            showPopupWindow(it)
        }

        binding.etBirth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                isBirthValid = s.toString().length == 4
                binding.layoutBirthyear.setBackgroundResource(
                    if (isBirthValid) R.drawable.bg_card_border_active
                    else R.drawable.bg_card_border_inactive
                )
                checkIfFormIsValid()
            }
        })

        binding.btnNext.setOnClickListener {
            if (isJobSelected && isBirthValid) {
                val year = binding.etBirth.text.toString().toInt()
                if (year <= 1930 || year >= 2026) {
                    isBirthValid = false
                    binding.etBirth.text.clear()
                    CommonUtils.showMultiLineCustomToast(
                        requireContext(),
                        "유효하지 않은 입력입니다",
                        "연도를 1930년도 이후나 2025년도 밑으로 입력해주세요"
                    )
                } else {
                    loginViewModel.updateUserJob(binding.tvJoblabel.text.toString())
                    loginViewModel.updateBirthYear(year)
                    findNavController().navigate(R.id.action_registerUserInfoFragment_to_registerPasswordFragment)
                    binding.etBirth.text.clear()
                }
            }
        }

        binding.cvBirthyear.setOnClickListener {
            binding.etBirth.requestFocus()
            binding.etBirth.setSelection(binding.etBirth.text.length)
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etBirth, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.root.setOnTouchListener { _, _ ->
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etBirth.windowToken, 0)
            binding.etBirth.clearFocus()
            false
        }
    }

    private fun showPopupWindow(view: View) {
        // 이미 열려있으면 닫고 반환
        if (popupWindow != null && popupWindow?.isShowing == true) {
            popupWindow?.dismiss()
            return
        }

        // 새 팝업 생성
        val inflater = LayoutInflater.from(requireContext())
        val popupBinding = PopupJobMenuBinding.inflate(inflater)

        // 새 PopupWindow 인스턴스 생성
        popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true  // focusable을 true로 변경
        )

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        popupWindow?.width = (displayMetrics.widthPixels * 0.85).toInt()
        popupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow?.isOutsideTouchable = true
        popupWindow?.isTouchable = true

        // 팝업 표시
        popupWindow?.showAsDropDown(view)

        val clickListener = View.OnClickListener { clickedView ->
            val jobTitle = when (clickedView.id) {
                R.id.popupItemStudent -> "무직"
                R.id.popupItemEmployee -> "직장인"
                R.id.popupItemSelfEmployed -> "자영업자"
                else -> return@OnClickListener
            }
            binding.tvJoblabel.text = jobTitle
            checkIfJobSelected()
            popupWindow?.dismiss()
        }

        popupBinding.popupItemStudent.setOnClickListener(clickListener)
        popupBinding.popupItemEmployee.setOnClickListener(clickListener)
        popupBinding.popupItemSelfEmployed.setOnClickListener(clickListener)

        // 팝업이 닫힐 때 참조 정리
        popupWindow?.setOnDismissListener {
            popupWindow = null
        }
    }

    private fun checkIfJobSelected() {
        isJobSelected = binding.tvJoblabel.text.toString() != "직업을 선택해주세요"
        binding.layoutJobSelect.setBackgroundResource(
            if (isJobSelected) R.drawable.bg_card_border_active
            else R.drawable.bg_card_border_inactive
        )
        checkIfFormIsValid()
    }

    private fun checkIfFormIsValid() {
        binding.btnNext.isEnabled = isJobSelected && isBirthValid
        val colorRes = if (binding.btnNext.isEnabled)
            R.color.colorButtonActive
        else
            R.color.colorButtonInactive
        binding.btnNext.setBackgroundColor(ContextCompat.getColor(requireContext(), colorRes))
    }
}