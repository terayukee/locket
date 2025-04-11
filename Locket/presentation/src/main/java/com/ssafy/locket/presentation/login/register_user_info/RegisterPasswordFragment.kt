package com.ssafy.locket.presentation.login.register_user_info

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentRegisterPasswordBinding

class RegisterPasswordFragment : BaseFragment<FragmentRegisterPasswordBinding>(
    FragmentRegisterPasswordBinding::bind,
    R.layout.fragment_register_password
) {
    private lateinit var passwordInputHandler: PasswordInputHandler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPassword()
        initEvent()
        setupNumberButtons()
        setupClearButton()
    }

    private fun initEvent() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun initPassword(){
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
            val password = passwordInputHandler.getPassword().toString()
            val bundle = Bundle().apply { putString("password", password) }
            findNavController().navigate(R.id.action_registerPasswordFragment_to_confirmPasswordFragment,bundle)
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

    // 지우기 버튼 초기화
    private fun setupClearButton() {
        binding.btnClear.setOnClickListener {
            passwordInputHandler.removeLastDigit()
        }
    }
}