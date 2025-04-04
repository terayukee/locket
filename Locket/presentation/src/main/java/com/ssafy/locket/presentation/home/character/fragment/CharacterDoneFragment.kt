package com.ssafy.locket.presentation.home.character.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentCharacterDoneBinding
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterInfoState
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterViewModel
import kotlinx.coroutines.launch

class CharacterDoneFragment : BaseFragment<FragmentCharacterDoneBinding>(
    FragmentCharacterDoneBinding::bind,
    R.layout.fragment_character_done
) {
    private val characterViewModel: CharacterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    private fun initUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            characterViewModel.characterInfo.collect { uiState ->
                if(uiState is CharacterInfoState.Success) {
                    binding.tvCharacterName.text = getString(R.string.home_character_gifticon_character, uiState.characterInfo.name)
                    binding.tvCharacterMessage.text = getString(R.string.home_character_gifticon_character_message,uiState.characterInfo.name)
                }
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}