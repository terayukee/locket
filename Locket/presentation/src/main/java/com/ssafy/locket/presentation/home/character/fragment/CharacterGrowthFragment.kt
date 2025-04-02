package com.ssafy.locket.presentation.home.character.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.model.home.character.CharacterAction
import com.ssafy.locket.model.home.character.characterCoolTime
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    private fun initUI() {
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
        }

        binding.ivMissionToyBg.setOnClickListener {
            characterViewModel.growCharacter(CharacterAction.Play)
            binding.ivMissionToyBg.isEnabled = false
            startTimer()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            characterViewModel.characterInfo.collect { uiState ->
                if(uiState is CharacterInfoState.Success) {
                    if(uiState.characterInfo.level == 3) characterViewModel.completeCharacter()
                    binding.tvCharacterName.text = uiState.characterInfo.name
                    minRemain = uiState.characterInfo.toy.remainingTimeMinutes + 1
                    binding.tvMissionToyQuantity.text = if(minRemain > 0) getString(R.string.home_character_toy_remain_time, minRemain) else "사용 가능"
                    binding.tvCharacterLevel.text = getString(R.string.home_character_level, uiState.characterInfo.level)
                    binding.tvCharacterPercent.text = getString(R.string.home_character_exp_percent, uiState.characterInfo.expPercentage)
                    binding.tvMissionFoodQuantity.text = getString(R.string.home_character_food_remain_count, uiState.characterInfo.foodCount)
                    binding.progressBar.progress = uiState.characterInfo.expPercentage.toInt()
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
                if (isTimerRunning && minRemain > 0) {
                    binding.tvMissionToyQuantity.text =
                        getString(R.string.home_character_toy_remain_time, minRemain)
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