package com.ssafy.locket.presentation.login.register_user_info

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentRegisterUserInfoBinding
import com.ssafy.locket.presentation.databinding.PopupJobMenuBinding
import com.ssafy.locket.presentation.utils.CommonUtils

class RegisterUserInfoFragment : BaseFragment<FragmentRegisterUserInfoBinding>(
    FragmentRegisterUserInfoBinding::bind,
    R.layout.fragment_register_user_info
) {

    //버튼 색깔 지정하기 위한 변수
    var isJobSelected = false
    var isBirthValid = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeVariable()
        initEvent()
    }

    fun initializeVariable(){
        isJobSelected = false
        isBirthValid = false
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivJobSelect.setOnClickListener {
            showPopupWindow(it)
        }

        binding.etBirth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                // 4자리 숫자가 입력되었는지 확인
                isBirthValid = editable.toString().length == 4
                if (isBirthValid) {
                    binding.layoutBirthyear.setBackgroundResource(R.drawable.bg_card_border_active) // 선택된 상태 테두리
                } else {
                    binding.layoutBirthyear.setBackgroundResource(R.drawable.bg_card_border_inactive) // 기본 테두리
                }
                checkIfFormIsValid()  // 양식이 유효한지 확인하는 함수 호출
            }
        })

        binding.btnNext.setOnClickListener {
            if (isJobSelected && isBirthValid) {
                if(binding.etBirth.text.toString().toInt()<=1930||binding.etBirth.text.toString().toInt()>=2026){
                    isBirthValid = false
                    binding.etBirth.text.clear()
                    CommonUtils.showMultiLineCustomToast(requireContext(), "유효하지 않은 입력입니다", "연도를 1930년도 이후나 2025년도 밑으로 입력해주세요")
//                    Toast.makeText(requireContext(),"연도를 1930년도 이후나 2025년도 밑으로 입력해주세요",Toast.LENGTH_LONG).show()
                }
                else{
                    findNavController().navigate(R.id.action_registerUserInfoFragment_to_registerPasswordFragment)
                    binding.etBirth.text.clear()
                }
            }
        }
    }

    private fun showPopupWindow(view: View) {
        val inflater = LayoutInflater.from(requireContext())
        val popupBinding = PopupJobMenuBinding.inflate(inflater) // ViewBinding 사용

        // PopupWindow 설정
        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        //크기 설정
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val popupWidth = (screenWidth * 0.85).toInt()  // 화면 너비의 90% 크기로 설정
        popupWindow.width = popupWidth
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(view, -view.x.toInt(), 0)

        val clickListener = View.OnClickListener { clickedView ->
            val jobTitle = when (clickedView.id) {
                R.id.popupItemStudent -> "학생/주부/무직"
                R.id.popupItemEmployee -> "직장인"
                R.id.popupItemSelfEmployed -> "자영업"
                else -> return@OnClickListener
            }
            binding.tvJoblabel.text = jobTitle
            checkIfJobSelected()
            popupWindow.dismiss()
        }
        popupBinding.popupItemStudent.setOnClickListener(clickListener)
        popupBinding.popupItemEmployee.setOnClickListener(clickListener)
        popupBinding.popupItemSelfEmployed.setOnClickListener(clickListener)
    }

    private fun checkIfJobSelected() {
        isJobSelected = binding.tvJoblabel.text.toString() != "직업을 선택해주세요"
        if (isJobSelected) {
            binding.layoutJobSelect.setBackgroundResource(R.drawable.bg_card_border_active) // 선택된 상태 테두리
        } else {
            binding.layoutJobSelect.setBackgroundResource(R.drawable.bg_card_border_inactive) // 기본 테두리
        }
        checkIfFormIsValid()
    }

    private fun checkIfFormIsValid() {
        // 직업이 선택되었고, 생년월일이 4자리 숫자로 입력되었을 때만 버튼을 활성화
        if (isJobSelected && isBirthValid) {
            binding.btnNext.isEnabled = true
            binding.btnNext.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorButtonActive))  // 활성화된 색상
        } else {
            binding.btnNext.isEnabled = false
            binding.btnNext.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorButtonInactive))  // 비활성화된 색상
        }
    }
}