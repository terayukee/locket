package com.ssafy.locket.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.user.UserApiClient
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentDeleteAccountBinding
import com.ssafy.locket.presentation.home.UserInfoViewModel
import com.ssafy.locket.presentation.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class DeleteAccountFragment : BaseFragment<FragmentDeleteAccountBinding>(
    FragmentDeleteAccountBinding::bind,
    R.layout.fragment_delete_account
) {
    //회원 정보 받아오기
    private val userInfoViewModel: UserInfoViewModel by activityViewModels()
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnDelete.setOnClickListener {
            lifecycleScope.launch {
                UserApiClient.instance.unlink { error ->
                    if (error != null) {
                        Log.e("DeleteAccount", "카카오 연결 해제 실패", error)
                    } else {
                        Log.i("DeleteAccount", "카카오 연결 해제 성공")
                        lifecycleScope.launch {
                            val user = userDataStoreSource.user.first()
                            user?.let {
                                userInfoViewModel.deleteUser(user.userId.toLong())
                            }
                            // 3. 로그인 화면 이동 (UI는 메인 스레드)
                            withContext(Dispatchers.Main) {
                                val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                startActivity(intent)
                                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                                requireActivity().finishAffinity()
                            }
                        }
                    }
                }
            }
        }
    }
}