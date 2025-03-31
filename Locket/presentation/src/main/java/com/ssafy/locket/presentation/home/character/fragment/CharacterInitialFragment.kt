package com.ssafy.locket.presentation.home.character.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentCharacterInitialBinding

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
            findNavController().navigate(R.id.action_characterInitialFragment_to_characterGrowthFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}