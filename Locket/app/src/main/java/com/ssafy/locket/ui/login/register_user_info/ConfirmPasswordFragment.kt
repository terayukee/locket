package com.ssafy.locket.ui.login.register_user_info

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentConfirmPasswordBinding

class ConfirmPasswordFragment : BaseFragment<FragmentConfirmPasswordBinding>(
    FragmentConfirmPasswordBinding::bind,
    R.layout.fragment_confirm_password
) {
    private lateinit var passwordInputHandler: PasswordInputHandler
    private var savedPassword: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        setupNumberButtons()
        setupClearButton()
    }

    private fun initEvent() {
        savedPassword = arguments?.getString("password")
        passwordInputHandler = PasswordInputHandler(
            arrayOf(
                binding.vPasswordDot1,
                binding.vPasswordDot2,
                binding.vPasswordDot3,
                binding.vPasswordDot4,
                binding.vPasswordDot5,
                binding.vPasswordDot6
            ),
            maxPasswordLength = 6
        ) {
            checkPassword(passwordInputHandler.getPassword().toString())
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    // 숫자 버튼 초기화
    private fun setupNumberButtons() {
        val numberButtonIds = arrayOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
            R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9
        )
        numberButtonIds.forEach { buttonId ->
            binding.root.findViewById<Button>(buttonId).setOnClickListener {
                passwordInputHandler.onNumberClicked((it as Button).text.toString())
            }
        }
    }
    private fun checkPassword(inputPassword: String) {
        if (inputPassword == savedPassword) {
            // 비밀번호가 맞으면 다음 화면으로 이동
            findNavController().navigate(R.id.action_confirmPasswordFragment_to_registerBiometricsFragment)
        } else {
            Toast.makeText(requireContext(), "다시 처음부터 비밀 번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            passwordInputHandler.clearPassword()
            findNavController().popBackStack() // 비밀번호 틀리면 이전 페이지로 돌아가기
        }
    }

    private fun setupClearButton() {
        binding.btnClear.setOnClickListener {
            passwordInputHandler.removeLastDigit()
        }
    }
}