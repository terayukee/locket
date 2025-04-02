package com.ssafy.locket.presentation.mypage

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentMyPageBinding
import com.ssafy.locket.presentation.home.UserInfoState
import com.ssafy.locket.presentation.home.UserInfoViewModel
import kotlinx.coroutines.launch

class MyPageFragment : BaseFragment<FragmentMyPageBinding>(
    FragmentMyPageBinding::bind,
    R.layout.fragment_my_page
) {
    private val userViewModel: UserInfoViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        observeModel()
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


    fun observeModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.userInfo.collect { user ->
                    if(user is UserInfoState.Success) {
                        Log.d("UserFragment", "User: ${user.userInfo.nickname}")
                        //binding.tvUserName.text = user.userInfo.nickname
                    }
                }
            }
        }
    }
}