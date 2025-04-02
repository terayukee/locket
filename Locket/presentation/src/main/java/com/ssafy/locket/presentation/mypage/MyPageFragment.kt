package com.ssafy.locket.presentation.mypage

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentMyPageBinding
import com.ssafy.locket.presentation.home.UserInfoState
import com.ssafy.locket.presentation.home.UserInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(
    FragmentMyPageBinding::bind,
    R.layout.fragment_my_page
) {
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initView()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivEdit.setOnClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_editUserInfoFragment)
        }
        binding.ivLogoutMove.setOnClickListener {
            val dialogFragment = LogoutDialogFragment()
            dialogFragment.show(parentFragmentManager, "logout_dialog")
        }
        binding.ivDeleteMove.setOnClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_deleteAccountFragment)
        }
    }

    fun initView() {
        lifecycleScope.launch {
            val user = userDataStoreSource.user.first() // 최초 값 한 번만 가져오기
            user?.let {
                binding.tvProfileName.text = it.nickname  // nickname을 TextView에 설정
                binding.tvProfileJob.text = "직업 " + it.userJob
                binding.tvProfileAge.text = "나이 " + (LocalDate.now().year + 1 - it.birthYear).toString() + "세"
            }
            Log.d("SignFragment", "변한 값: $user")
        }
    }

}