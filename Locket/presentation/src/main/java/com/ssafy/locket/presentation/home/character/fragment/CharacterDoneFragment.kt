package com.ssafy.locket.presentation.home.character.fragment

import android.os.Bundle
import android.view.View
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentCharacterDoneBinding

class CharacterDoneFragment : BaseFragment<FragmentCharacterDoneBinding>(
    FragmentCharacterDoneBinding::bind,
    R.layout.fragment_character_done
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCharacterName.text = getString(R.string.home_character_gifticon_character, "소심한 반짝반짝냥")
//        binding.tvCharacterName.text = getString(R.string.home_character_gifticon_character, "소심한 반짝냥")
//        binding.tvCharacterName.text = getString(R.string.home_character_gifticon_character, "소심한냥")
    }
}