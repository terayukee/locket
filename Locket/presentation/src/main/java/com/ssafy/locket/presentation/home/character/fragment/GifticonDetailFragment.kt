package com.ssafy.locket.presentation.home.character.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentCharacterDoneBinding
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterInfoState
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterViewModel
import com.ssafy.locket.presentation.home.character.viewmodel.GifticonsViewModel
import com.ssafy.locket.presentation.home.character.viewmodel.SelectedGifticonState
import kotlinx.coroutines.launch

class GifticonDetailFragment : BaseFragment<FragmentCharacterDoneBinding>(
    FragmentCharacterDoneBinding::bind,
    R.layout.fragment_gifticon_detail
) {
    private val gifticonViewModel: GifticonsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    private fun initUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            gifticonViewModel.selectedGifticon.collect { uiState ->
                if(uiState is SelectedGifticonState.Selected) {
                    binding.tvCharacterName.text = getString(R.string.home_character_gifticon_character, uiState.gifticon.characterName)
                    binding.tvCharacterMessage.text = getString(R.string.home_character_gifticon_character_message, uiState.gifticon.characterName)
                }
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onStop() {
        super.onStop()
        gifticonViewModel.clearGifticon()
    }
}