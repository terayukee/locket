package com.ssafy.locket.presentation.home.character.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentCharacterInitialBinding
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterViewModel
import com.ssafy.locket.presentation.home.character.viewmodel.NavigationEvent
import kotlinx.coroutines.launch

private const val TAG = "CharacterInitialFragmen"
class CharacterInitialFragment : BaseFragment<FragmentCharacterInitialBinding>(
    FragmentCharacterInitialBinding::bind,
    R.layout.fragment_character_initial
) {
    private val characterViewModel: CharacterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                characterViewModel.navigationEvent.collect { uiState ->
                    if(uiState is NavigationEvent.MoveToFragment) {
                        Log.d(TAG, "onViewCreated: move initail to growth")
                        findNavController().navigate(R.id.action_characterInitialFragment_to_characterGrowthFragment)
                    }
                }
            }
        }

    }


    fun initEvent(){
        binding.btnCharacterInit.setOnClickListener {
            characterViewModel.createCharacter()
//            findNavController().navigate(R.id.action_characterInitialFragment_to_characterGrowthFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}