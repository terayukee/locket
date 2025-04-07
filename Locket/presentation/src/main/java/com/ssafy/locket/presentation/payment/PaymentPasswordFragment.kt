package com.ssafy.locket.presentation.payment

import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentPaymentPasswordBinding
import com.ssafy.locket.presentation.login.register_user_info.PasswordInputHandler
import com.ssafy.locket.presentation.payment.viewmodel.PaymentPasswordViewModel
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import com.ssafy.locket.ui.payment.viewmodel.RecertifyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.security.KeyStore
import javax.crypto.KeyGenerator

private const val TAG = "PaymentPasswordFragment"
@AndroidEntryPoint
class PaymentPasswordFragment : BaseFragment<FragmentPaymentPasswordBinding>(
    FragmentPaymentPasswordBinding::bind,
    R.layout.fragment_payment_password
) {
    private lateinit var passwordInputHandler: PasswordInputHandler
    private val viewModel: RecertifyViewModel by activityViewModels()
    private val paymentPasswordViewModel: PaymentPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initPassword()
        initEvent()

        viewLifecycleOwner.lifecycleScope.launch {
            paymentPasswordViewModel.isPasswordVerify.collect {
                Log.d(TAG,it.toString())
                if(it) certifymove()
                else {
                    CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, "비밀번호가 틀렸습니다. 다시 입력해주세요.")
                    passwordInputHandler.clearPassword()
                }
            }
        }
    }

    fun initView(){
        if(viewModel.recertify.value==1) {
            binding.tvTitle.text= "재설정을 위해 비밀번호를 입력해주세요"
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
//            certifymove()
            paymentPasswordViewModel.checkPassword(passwordInputHandler.getPassword().toString().toInt())
        }
        setupNumberButtons()
        setupClearButton()
    }

    fun certifymove(){
        if(viewModel.recertify.value==1) {
            showBiometricPrompt()
        }
        else{
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.paymentPasswordFragment, true) // 현재 화면을 스택에서 제거
                .build()
            findNavController().navigate(R.id.action_paymentPasswordFragment_to_nfcPaymentFragment, null, navOptions)
        }
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

    //지문 인식 관련 코드
    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    showToast("지문 인증이 재설정되었습니다.")
                    resetBiometricKey()
                    findNavController().popBackStack()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showToast("지문 인증 실패: $errString")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showToast("지문이 일치하지 않습니다. 다시 시도해주세요.")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("지문 인증")
            .setSubtitle("등록된 지문을 사용하여 인증하세요.")
            .setNegativeButtonText("취소")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
    private fun resetBiometricKey() {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            keyStore.deleteEntry("biometric_key") // 기존 키 삭제
            generateSecretKey() // 새 키 생성
            viewModel.updateRecertify(0)
        } catch (e: Exception) {
            showToast("키 재설정 중 오류 발생: ${e.message}")
        }
    }

    private fun generateSecretKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            "biometric_key",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }
}