package com.ssafy.locket.presentation.login.register_user_info

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.biometric.BiometricManager
import androidx.navigation.fragment.findNavController
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
        // BiometricManager로 생체 인증 가능 여부 체크
        val biometricManager = BiometricManager.from(requireContext())

        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showToast("이 장치의 지문을 등록하였습니다")

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
                redirectToBiometricSettings()
            }
            else -> {
                showToast("생체 인식 인증 실패")
            }
        }
    }

    private fun moveToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    private fun redirectToBiometricSettings() {
        val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
        startActivity(intent)
    }
}