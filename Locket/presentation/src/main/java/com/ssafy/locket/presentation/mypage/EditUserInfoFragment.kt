package com.ssafy.locket.presentation.mypage

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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentEditUserInfoBinding
import com.ssafy.locket.presentation.databinding.PopupJobMenuBinding

class EditUserInfoFragment : BaseFragment<FragmentEditUserInfoBinding>(
    FragmentEditUserInfoBinding::bind,
    R.layout.fragment_edit_user_info
) {
    //수정하기 위한 여부
    var isBirthValid = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inititlView()
        initEvent()
    }

    fun inititlView(){
        binding.layoutJob.setBackgroundResource(R.drawable.bg_card_border_active)
        binding.layoutAge.setBackgroundResource(R.drawable.bg_card_border_active) // 선택된 상태 테두리
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivJobDropdown.setOnClickListener {
            showPopupWindow(it)
        }
        binding.editAge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                checkIfFormIsValid()
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                // 4자리 숫자가 입력되었는지 확인
                isBirthValid = editable.toString().length == 4
                if (isBirthValid) {
                    binding.layoutAge.setBackgroundResource(R.drawable.bg_card_border_active) // 선택된 상태 테두리
                } else {
                    binding.layoutAge.setBackgroundResource(R.drawable.bg_card_border_inactive) // 기본 테두리
                }
                checkIfFormIsValid()  // 양식이 유효한지 확인하는 함수 호출
            }
        })

        binding.btnChange.setOnClickListener {
            if (isBirthValid) {
                if(binding.editAge.text.toString().toInt()<=1930||binding.editAge.text.toString().toInt()>=2026){
                    isBirthValid = false
                    binding.editAge.text.clear()
                    Toast.makeText(requireContext(),"연도를 1930년도 이후나 2025년도 수정해 입력해주세요", Toast.LENGTH_LONG).show()
                }
                else{
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.editUserInfoFragment, true) // Remove current fragment from back stack
                        .setLaunchSingleTop(true) // Ensure only one instance of the destination
                        .build()
                    findNavController().navigate(
                        R.id.action_editUserInfoFragment_to_myPageFragment,
                        null,
                        navOptions
                    )
                    binding.editAge.text.clear()
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
            binding.tvJobSelect.text = jobTitle
            popupWindow.dismiss()
        }
        popupBinding.popupItemStudent.setOnClickListener(clickListener)
        popupBinding.popupItemEmployee.setOnClickListener(clickListener)
        popupBinding.popupItemSelfEmployed.setOnClickListener(clickListener)
    }

    private fun checkIfFormIsValid() {
        // 직업이 선택되었고, 생년월일이 4자리 숫자로 입력되었을 때만 버튼을 활성화
        if (isBirthValid) {
            binding.btnChange.isEnabled = true
            binding.btnChange.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorButtonActive))  // 활성화된 색상
        } else {
            binding.btnChange.isEnabled = false
            binding.btnChange.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorButtonInactive))  // 비활성화된 색상
        }
    }
}