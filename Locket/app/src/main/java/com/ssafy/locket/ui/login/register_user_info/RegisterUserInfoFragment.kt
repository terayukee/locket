package com.ssafy.locket.ui.login.register_user_info

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentRegisterUserInfoBinding


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
            showPopupMenu(it)
        }

        binding.etBirth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                // 4자리 숫자가 입력되었는지 확인
                isBirthValid = editable.toString().length == 4
                checkIfFormIsValid()  // 양식이 유효한지 확인하는 함수 호출
            }
        })

        binding.btnNext.setOnClickListener {
            if (isJobSelected && isBirthValid) {
                if(binding.etBirth.text.toString().toInt()<=1930||binding.etBirth.text.toString().toInt()>=2026){
                    isBirthValid = false
                    binding.etBirth.text.clear()
                    Toast.makeText(requireContext(),"연도를 1930년도 이후나 2025년도 밑으로 입력해주세요",Toast.LENGTH_LONG).show()
                }
                else{
                    findNavController().navigate(R.id.action_registerUserInfoFragment_to_registerPasswordFragment)
                    binding.etBirth.text.clear()
                }
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        requireActivity().menuInflater.inflate(R.menu.job_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            binding.tvJoblabel.text = item.title
            checkIfJobSelected()  // 직업 선택 시 체크
            true
        }
        popupMenu.show()
    }

    private fun checkIfJobSelected() {
        isJobSelected = binding.tvJoblabel.text.toString() != "직업을 선택해주세요"
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