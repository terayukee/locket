package com.ssafy.locket.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.common.view.MainActivity
import com.ssafy.locket.presentation.databinding.FragmentSignInBinding
import kotlinx.coroutines.launch


private const val TAG = "SignInFragment"
class SignInFragment : BaseFragment<FragmentSignInBinding>(
    FragmentSignInBinding::bind,
    R.layout.fragment_sign_in
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.ivKakaoMove.setOnClickListener {
            kakaoLogin()
            //findNavController().navigate(R.id.action_signInFragment_to_registerUserInfoFragment)
        }
    }

    private fun kakaoLogin() {
        // 카카오톡이 설치되어 있는지 확인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            // 카카오톡으로만 로그인 시도
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)
                    // 사용자가 로그인 취소한 경우 아무 작업도 하지 않음
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    Log.e(TAG, "에러 타입: ${error::class.java.simpleName}")
                    showToast("카카오톡 로그인에 실패했습니다. 다시 시도해주세요.")
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공: ${token.accessToken}")
                    performKakaoLogin(token.accessToken)
                }
            }
        } else {
            Log.e(TAG, "카카오톡이 설치되어 있지 않습니다.")
            showToast("카카오톡이 설치되어 있지 않습니다. 앱을 설치한 후 다시 시도해주세요.")
        }
    }

    /**
     * 카카오 SDK로 받은 accessToken을 서버에 전달하여 로그인 처리하는 함수
     */
    private fun performKakaoLogin(accessToken: String) {
        lifecycleScope.launch {
            try {
                // GET 방식으로 서버에 로그인 요청: accessToken을 URL 경로의 {code}에 매핑
                /*val response = RetrofitUtil.userService.kakaoLogin(accessToken)

                if (response.isSuccessful) {
                    // Retrofit의 Response 객체에서 HTTP 응답 헤더를 추출
                    val httpHeaders = response.headers().toMultimap()
                    // "Set-Cookie" 헤더에 들어있는 쿠키 값들을 추출
                    val httpCookies = response.headers().values("Set-Cookie")

                    // HTTP 응답 헤더와 쿠키 로그 출력
                    Log.d(TAG, "HTTP 응답 헤더: $httpHeaders")
                    Log.d(TAG, "HTTP 응답 쿠키: $httpCookies")

                    val sharedPref = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                    val saveToken = sharedPref.getString(KeyAccessToken, "") ?: ""
                    Log.d(TAG, "${httpHeaders["access"]?.get(0)}")
                    val userToken = httpHeaders["access"]?.get(0)
                    SharedPreferencesUtil.saveAccessToken(userToken!!)
                    with(sharedPref.edit()) {
                        putString(KeyAccessToken, userToken)
                        apply()
                    }
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)

                } else {
                    Log.e(TAG, "서버 카카오 로그인 실패: ${response.errorBody()?.string()}")
                }*/
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "서버 통신 중 에러 발생", e)
            }
        }
    }
}