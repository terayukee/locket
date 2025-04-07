package com.ssafy.locket.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val notificationCheckViewModel: NotificationCheckViewModel by activityViewModels()
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    private var isClick = false // 중복 클릭 방지 변수
    private var isNotification = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        observeNotificationState()
        observeLoginState()
    }

    override fun onResume() {
        super.onResume()
        isClick = false // 화면이 다시 활성화될 때 클릭 가능하도록 초기화
    }

    fun initEvent(){
        binding.ivKakaoMove.setOnClickListener {
            if (!isClick) {
                isClick = true // 클릭 방지 활성화
                kakaoLogin()
            }
        }
    }

    private fun kakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            // 카카오톡으로만 로그인 시도
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        isClick = false
                        return@loginWithKakaoTalk
                    }
                    showToast("카카오톡 로그인에 실패했습니다. 다시 시도해주세요.")
                    isClick = false
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공: ${token.accessToken}")
                    lifecycleScope.launch {
                        Log.d(TAG,"로그인 관련 ${token.accessToken}")
                        userDataStoreSource.saveKakaoAccessToken(token.accessToken)
                        loginViewModel.performKakaoLogin(token.accessToken)
                    }
                }
            }
        } else {
            Log.e(TAG, "카카오톡이 설치되어 있지 않습니다.")
            showToast("카카오톡이 설치되어 있지 않습니다. 앱을 설치한 후 다시 시도해주세요.")
            isClick = false // 클릭 가능 상태로 변경
        }
    }

    private fun observeNotificationState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                notificationCheckViewModel.isIconClicked.collect {
                    if(it) isNotification = true
                }
            }

        }
    }

    private fun observeLoginState() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginState.collect { status ->
                when (status) {
                    is LoginStatus.Success -> {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        if (isNotification) {
                            intent.putExtra("notification", "notification")
                        }
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    is LoginStatus.Error -> {
                        lifecycleScope.launch {
                            val token = userDataStoreSource.kakaoAccessToken.first()
                            loginViewModel.updateAccessToken(token ?: "")
                        }
                        val currentDestination = findNavController().currentDestination?.id
                        if (currentDestination == R.id.signInFragment) {
                            findNavController().navigate(R.id.action_signInFragment_to_registerUserInfoFragment)
                        }
                        loginViewModel.resetLoginState()
                        loginViewModel.resetLoginState()
                    }
                    LoginStatus.Idle -> {

                    }
                }
                isClick = false
            }
        }
    }

}
