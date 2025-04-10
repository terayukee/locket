package com.ssafy.locket.presentation.home.character.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentCharacterDoneBinding
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterInfoState
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterViewModel
import com.ssafy.locket.presentation.home.character.viewmodel.CompleteGifticonState
import kotlinx.coroutines.launch

private const val TAG = "CharacterDoneFragment"
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
            characterViewModel.completeGift.collect { uiState ->
                if(uiState is CompleteGifticonState.Success) {
                    binding.tvCharacterName.text = getString(R.string.home_character_gifticon_character, uiState.gifticon.characterName)
                    binding.tvCharacterMessage.text = getString(R.string.home_character_gifticon_character_message,uiState.gifticon.characterName)
//                    Log.d(TAG, "initUI: Character Success ${uiState.gifticon.characterName}")
                }
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onPause() {
        super.onPause()
        characterViewModel.clearCharacter()
    }
}