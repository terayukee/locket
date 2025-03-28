package com.ssafy.locket.presentation.login.register_user_info

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.biometric.BiometricManager
import androidx.navigation.fragment.findNavController
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import javax.crypto.KeyGenerator
import com.ssafy.locket.presentation.common.view.MainActivity
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentRegisterBiometricsBinding

class RegisterBiometricsFragment : BaseFragment<FragmentRegisterBiometricsBinding>(
    FragmentRegisterBiometricsBinding::bind,
    R.layout.fragment_register_biometrics
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    private fun initEvent() {
        binding.btnLater.setOnClickListener {
            moveToMainActivity()
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnRegister.setOnClickListener {
            initBiometrics()
        }
    }

    private fun initBiometrics() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                generateSecretKey() // 새 키 생성
                showToast("지문이 등록되었습니다.")
                moveToMainActivity()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                showToast("이 장치에서는 생체 인식이 지원되지 않습니다.")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                showToast("생체 인식 하드웨어가 사용 불가능합니다.")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                showToast("생체 인식 정보가 등록되지 않았습니다.")
            }
            else -> {
                showToast("생체 인식 인증 실패")
            }
        }
    }

    private fun moveToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        requireActivity().finishAffinity()
    }

    // 📌 1. Keystore에서 SecretKey 생성 (최초 실행 시)
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
            .setUserAuthenticationRequired(true) // 지문 인증 필수
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

}