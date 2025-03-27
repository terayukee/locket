package com.ssafy.locket.presentation.home.character.fragments

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

    }
}