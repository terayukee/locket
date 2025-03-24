package com.ssafy.locket.ui.home.character

import android.os.Bundle
import android.view.View
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
        binding.btnCharacterInit.setOnClickListener {
            findNavController().navigate(R.id.characterGrowthFragment)
        }
    }
}