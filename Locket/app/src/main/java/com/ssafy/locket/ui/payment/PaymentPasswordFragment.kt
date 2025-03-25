package com.ssafy.locket.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentNfcPaymentBinding
import com.ssafy.locket.databinding.FragmentPaymentPasswordBinding
import com.ssafy.locket.ui.login.register_user_info.PasswordInputHandler

class PaymentPasswordFragment : BaseFragment<FragmentPaymentPasswordBinding>(
    FragmentPaymentPasswordBinding::bind,
    R.layout.fragment_payment_password
) {
    private lateinit var passwordInputHandler: PasswordInputHandler
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPassword()
        initEvent()
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
            findNavController().navigate(R.id.action_paymentPasswordFragment_to_nfcPaymentFragment)
        }
        setupNumberButtons()
        setupClearButton()
    }

    fun initEvent(){
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

    // 지우기 버튼 초기화
    private fun setupClearButton() {
        binding.btnClear.setOnClickListener {
            passwordInputHandler.removeLastDigit()
        }
    }
}