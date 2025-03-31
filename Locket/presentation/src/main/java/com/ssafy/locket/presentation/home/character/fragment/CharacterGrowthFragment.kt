package com.ssafy.locket.presentation.home.character.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentCharacterGrowthBinding

private const val TAG = "CharacterGrowthFragment"
class CharacterGrowthFragment: BaseFragment<FragmentCharacterGrowthBinding>(
    FragmentCharacterGrowthBinding::bind,
    R.layout.fragment_character_growth
){

    private val handler = Handler(Looper.getMainLooper())
    private var minRemain = 2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startTimer()


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvMissionToyQuantity.text = getString(R.string.home_character_toy_remain_time, minRemain)

        binding.tvCharacterName.setOnClickListener {
            findNavController().navigate(R.id.action_characterGrowthFragment_to_characterDoneFragment)
        }

        binding.tvCharacterName.text = getString(R.string.home_character_gifticon_character,"소심한 반짝냥")

        binding.btnGifticonBox.setOnClickListener {
            findNavController().navigate(R.id.action_characterGrowthFragment_to_giftCardListFragment)
        }
    }

    private fun startTimer() {
        val timerRunnable = object : Runnable {
            override fun run() {
                if (minRemain > 0) {
                    Log.d(TAG, "run: time reduce")
                    binding.tvMissionToyQuantity.text = getString(R.string.home_character_toy_remain_time, minRemain)
                    minRemain--

                    handler.postDelayed(this, 60000)
                } else {
                    Log.d(TAG, "run: handler removed")
                    handler.removeCallbacks(this)
                    binding.tvMissionToyQuantity.text = "사용 가능"
                }
            }
        }
        handler.postDelayed(timerRunnable, 60000)
    }
}