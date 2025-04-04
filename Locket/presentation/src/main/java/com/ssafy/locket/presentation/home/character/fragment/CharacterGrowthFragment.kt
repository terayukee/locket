package com.ssafy.locket.presentation.home.character.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ssafy.locket.model.home.character.CharacterAction
import com.ssafy.locket.model.home.character.testCharacterCoolTime
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentCharacterGrowthBinding
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterInfoState
import com.ssafy.locket.presentation.home.character.viewmodel.CharacterViewModel
import kotlinx.coroutines.launch

private const val TAG = "CharacterGrowthFragment"
class CharacterGrowthFragment: BaseFragment<FragmentCharacterGrowthBinding>(
    FragmentCharacterGrowthBinding::bind,
    R.layout.fragment_character_growth
){
    private var isTimerRunning = false
    private lateinit var timerRunnable: Runnable
    private val handler = Handler(Looper.getMainLooper())
    private var minRemain = testCharacterCoolTime
    private val characterViewModel: CharacterViewModel by activityViewModels()
    private var gifResId: Int = -1
    private var imageResId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        gifResId = resources.getIdentifier("gif_character_level_1", "raw", requireContext().packageName)
//        imageResId = resources.getIdentifier("image_character_level_1", "drawable", requireContext().packageName)
        initUI()

    }

    private fun initUI() {
//        Glide.with(requireContext())
//            .load(characterImageName)
//            .into(binding.ivCharacter)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvCharacterName.setOnClickListener {
            findNavController().navigate(R.id.action_characterGrowthFragment_to_characterDoneFragment)
        }

        binding.btnGifticonBox.setOnClickListener {
            findNavController().navigate(R.id.action_characterGrowthFragment_to_gifticonListFragment)
        }

        binding.ivMissionFoodBg.setOnClickListener {
            characterViewModel.growCharacter(CharacterAction.Feed)
            Glide.with(requireContext())
                .load(gifResId)
                .placeholder(imageResId)
                .into(binding.ivCharacter)

            binding.ivCharacter.postDelayed({
                Glide.with(requireContext())
                    .load(imageResId)
                    .into(binding.ivCharacter)
            }, 2000)
        }

        binding.ivMissionToyBg.setOnClickListener {
            characterViewModel.growCharacter(CharacterAction.Play)
            binding.ivMissionToyBg.isEnabled = false
            startTimer()
            Glide.with(requireContext())
                .load(gifResId)
                .placeholder(imageResId)
                .into(binding.ivCharacter)

            binding.ivCharacter.postDelayed({
                Glide.with(requireContext())
                    .load(imageResId)
                    .into(binding.ivCharacter)
            }, 2000)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            characterViewModel.characterInfo.collect { uiState ->
                if(uiState is CharacterInfoState.Success) {
                    if(uiState.characterInfo.level == 4) characterViewModel.completeCharacter()
                    binding.tvCharacterName.text = uiState.characterInfo.name
                    minRemain = uiState.characterInfo.toy.remainingTimeMinutes
                    binding.tvMissionToyQuantity.text = if(minRemain > 0) getString(R.string.home_character_toy_remain_time, minRemain) else "사용 가능"
                    binding.tvCharacterLevel.text = getString(R.string.home_character_level, uiState.characterInfo.level)
                    binding.tvCharacterPercent.text = getString(R.string.home_character_exp_percent, uiState.characterInfo.expPercentage)
                    binding.tvMissionFoodQuantity.text = getString(R.string.home_character_food_remain_count, uiState.characterInfo.foodCount)
                    binding.progressBar.progress = uiState.characterInfo.expPercentage.toInt()
                    val level = uiState.characterInfo.level

                    gifResId = resources.getIdentifier("gif_character_level_$level", "raw", requireContext().packageName)
                    imageResId = resources.getIdentifier("image_character_level_$level", "drawable", requireContext().packageName)

                    Glide.with(requireContext())
                        .load(imageResId)
                        .into(binding.ivCharacter)

                    if(uiState.characterInfo.foodCount == 0) binding.ivMissionFoodBg.isEnabled = false
                    if(uiState.characterInfo.toy.remainingTimeMinutes > 0) {
                        binding.ivMissionToyBg.isEnabled = false
                        startTimer()
                    } else {
                        binding.ivMissionToyBg.isEnabled = true
                    }
                } else {
                    // TODO 캐릭터 정보 불러오지 못했을 때 예외처리
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            characterViewModel.completeGift.collect {
                if (it) findNavController().navigate(R.id.action_characterGrowthFragment_to_characterDoneFragment)
            }
        }

    }

    private fun startTimer() {
        isTimerRunning = true

        timerRunnable = object : Runnable {
            override fun run() {
                if (isTimerRunning && minRemain == 0) {
                    binding.tvMissionToyQuantity.text =
                        getString(R.string.home_character_toy_remain_time, minRemain+1)
                    minRemain--

                    handler.postDelayed(this, 60000)
                } else {
                    stopTimer() // 타이머 종료
                    binding.ivMissionToyBg.isEnabled = true
                    binding.tvMissionToyQuantity.text = "사용 가능"
                }
            }
        }

        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        if (isTimerRunning) {
            isTimerRunning = false
            handler.removeCallbacks(timerRunnable)
        }
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
    }
}