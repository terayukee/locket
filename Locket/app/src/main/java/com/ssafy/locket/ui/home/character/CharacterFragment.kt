package com.ssafy.locket.ui.home.character

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentCharacterBinding

class CharacterFragment : BaseFragment<FragmentCharacterBinding>(
    FragmentCharacterBinding::bind,
    R.layout.fragment_character
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}