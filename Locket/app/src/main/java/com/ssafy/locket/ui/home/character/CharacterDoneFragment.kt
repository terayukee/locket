package com.ssafy.locket.ui.home.character

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentCharacterDoneBinding

class CharacterDoneFragment : BaseFragment<FragmentCharacterDoneBinding>(
    FragmentCharacterDoneBinding::bind,
    R.layout.fragment_character_done
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}