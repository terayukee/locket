package com.ssafy.locket.ui.home.character

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentCharacterGrowthBinding

class CharacterGrowthFragment: BaseFragment<FragmentCharacterGrowthBinding>(
    FragmentCharacterGrowthBinding::bind,
    R.layout.fragment_character_growth
){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}