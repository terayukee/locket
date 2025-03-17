package com.ssafy.locket.ui.mypage

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentEditUserInfoBinding

class EditUserInfoFragment : BaseFragment<FragmentEditUserInfoBinding>(
    FragmentEditUserInfoBinding::bind,
    R.layout.fragment_edit_user_info
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}