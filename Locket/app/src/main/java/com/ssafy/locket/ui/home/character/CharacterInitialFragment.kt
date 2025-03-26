package com.ssafy.locket.ui.home.character

import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentCharacterInitialBinding

class CharacterInitialFragment : BaseFragment<FragmentCharacterInitialBinding>(
    FragmentCharacterInitialBinding::bind,
    R.layout.fragment_character_initial
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.btnCharacterInit.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.characterInitialFragment, true)  // 현재 스택에서 지정한 프래그먼트를 제거
                .setLaunchSingleTop(true)              // 새 프래그먼트가 스택에 중복해서 쌓이지 않도록 설정
                .build()
            findNavController().navigate(R.id.characterGrowthFragment, null, navOptions)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}