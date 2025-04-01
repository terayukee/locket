package com.ssafy.locket.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.common.view.MainActivity
import com.ssafy.locket.presentation.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "SignInFragment"
@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>(
    FragmentSignInBinding::bind,
    R.layout.fragment_sign_in
) {
    private val loginViewModel: LoginViewModel by viewModels()
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        observeLoginState()
    }

    fun initEvent(){
        binding.ivKakaoMove.setOnClickListener {
            kakaoLogin()
        }
    }

    private fun kakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            // 카카오톡으로만 로그인 시도
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    Log.e(TAG, "에러 타입: ${error::class.java.simpleName}")
                    showToast("카카오톡 로그인에 실패했습니다. 다시 시도해주세요.")
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공: ${token.accessToken}")
                    lifecycleScope.launch {
                        val fcmToken = userDataStoreSource.fcmToken.first() // Flow에서 값 가져오기
                        Log.d(TAG,"로그인 관련"+token.accessToken)
                        Log.d(TAG,"fcm 관련"+fcmToken)
                        loginViewModel.performKakaoLogin(token.accessToken, fcmToken ?: "")
                    }
                }
            }
        } else {
            Log.e(TAG, "카카오톡이 설치되어 있지 않습니다.")
            showToast("카카오톡이 설치되어 있지 않습니다. 앱을 설치한 후 다시 시도해주세요.")
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginState.collect { isRegistered ->
                if (isRegistered) {
                    Log.d(TAG,"홈화면으로 갑니다")
                } else {
                    findNavController().navigate(R.id.action_signInFragment_to_registerUserInfoFragment) // 가입 필요하면 회원가입 화면으로 이동
                }
            }
        }
    }
}