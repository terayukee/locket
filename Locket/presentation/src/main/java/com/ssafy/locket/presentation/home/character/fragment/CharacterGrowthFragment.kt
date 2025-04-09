package com.ssafy.locket.presentation.home.character.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
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
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import kotlinx.coroutines.launch
import kotlin.math.min

private const val TAG = "CharacterGrowthFragment"

class CharacterGrowthFragment : BaseFragment<FragmentCharacterGrowthBinding>(
    FragmentCharacterGrowthBinding::bind,
    R.layout.fragment_character_growth
) {
    private var mContext: Context? = null
    private var isTimerRunning = false
    private lateinit var timerRunnable: Runnable
    private val timerHandler = Handler(Looper.getMainLooper())
    private var minRemain = testCharacterCoolTime
    private val characterViewModel: CharacterViewModel by activityViewModels()
    private var gifResId: Int = -1
    private var imageResId: Int = -1
    private lateinit var pendingGifRunnable: Runnable
    private val gifHandler = Handler(Looper.getMainLooper())
    private var isGifLoading = false

    private val clickHandler = Handler(Looper.getMainLooper())
    private var clickCount = 0

    private val CLICK_INTERVAL_THRESHOLD = 800 // 연속 클릭으로 간주할 최대 시간 간격 (밀리초)
    private var lastClickTime = 0L

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        characterViewModel.getCharacter()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnGifticonBox.setOnClickListener {
            findNavController().navigate(R.id.action_characterGrowthFragment_to_gifticonListFragment)
        }

        binding.ivMissionFoodBg.setOnClickListener {
            characterViewModel.growCharacter(CharacterAction.Feed)
        }

        binding.ivMissionToyBg.setOnClickListener {
            binding.ivMissionToyBg.isEnabled = false
            characterViewModel.growCharacter(CharacterAction.Play)
            startTimer()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            characterViewModel.characterInfo.collect { uiState ->
                if (uiState is CharacterInfoState.Success) {
                    if (uiState.characterInfo.level == 4) {
                        binding.ivMissionToyBg.isEnabled = false
                        binding.ivCharacterGrowthBg.isEnabled = false
                        characterViewModel.completeCharacter()
                    } else {
                        val level = uiState.characterInfo.level
                        gifResId = resources.getIdentifier(
                            "gif_character_level_$level",
                            "raw",
                            requireContext().packageName
                        )
                        imageResId = resources.getIdentifier(
                            "image_character_level_$level",
                            "drawable",
                            requireContext().packageName
                        )
                        if (uiState.characterInfo.isInit == false) {
                            mContext?.let {
                                Log.d(TAG, "initUI false : imageResId $imageResId level $level")
                                Glide.with(it)
                                    .load(gifResId)
                                    .placeholder(imageResId)
                                    .into(binding.ivCharacter)
                            }
                            loadCharacterAnimation()
                        } else {
                            mContext?.let { context ->
                                Log.d(TAG, "initUI else : imageResId $imageResId  level $level")
                                Glide.with(context)
                                    .load(imageResId)
                                    .into(binding.ivCharacter)
                            }
                        }
                    }
                    binding.tvCharacterName.text = uiState.characterInfo.name
                    minRemain = uiState.characterInfo.toy.remainingTimeMinutes

                    binding.tvCharacterLevel.text =
                        getString(R.string.home_character_level, uiState.characterInfo.level)
                    binding.tvCharacterPercent.text = getString(
                        R.string.home_character_exp_percent,
                        uiState.characterInfo.expPercentage
                    )
                    binding.tvMissionFoodQuantity.text = getString(
                        R.string.home_character_food_remain_count,
                        uiState.characterInfo.foodCount
                    )
                    binding.progressBar.progress = uiState.characterInfo.expPercentage.toInt()

                    if (uiState.characterInfo.foodCount == 0) binding.ivMissionFoodBg.isEnabled =
                        false
                    if (minRemain >= 0 && uiState.characterInfo.toy.isAvailable == false) {
                        binding.ivMissionToyBg.isEnabled = false
                        binding.tvMissionToyQuantity.text = getString(
                            R.string.home_character_toy_remain_time,
                            minRemain + 1
                        )
                        startTimer()
                    } else {
                        binding.ivMissionToyBg.isEnabled = true
                        binding.tvMissionToyQuantity.text = "사용 가능"
                        isTimerRunning = false
                    }
                } else if (uiState is CharacterInfoState.Empty) {
                    findNavController().navigate(R.id.action_characterGrowthFragment_to_characterDoneFragment)
                } else {
                    // TODO 캐릭터 정보 불러오지 못했을 때 예외처리
                    Log.d(
                        TAG,
                        "initUI: Error Character${(uiState as CharacterInfoState.Error).message}"
                    )
                    CommonUtils.showSingleLineCustomToast(
                        requireContext(),
                        ToastType.ERROR,
                        "네트워크 오류가 발생하였습니다. 잠시후 다시 시도해주세요."
                    )
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
        if (isTimerRunning == true) return
        isTimerRunning = true

        timerRunnable = object : Runnable {
            override fun run() {
                if (minRemain >= 0) {
                    Log.d(TAG, "run: isTimerRunning and minRemain $minRemain")
                    binding.tvMissionToyQuantity.text =
                        getString(R.string.home_character_toy_remain_time, minRemain + 1)
                    minRemain--

                    timerHandler.postDelayed(this, 60000)
                } else {
                    Log.d(TAG, "run: isTimerRunning and minRemain == 0")
                    stopTimer()
                    binding.ivMissionToyBg.isEnabled = true
                    binding.tvMissionToyQuantity.text = "사용 가능"
                }
            }
        }
        timerHandler.post(timerRunnable)
    }

    private fun loadCharacterAnimation() {
        isGifLoading = true
        pendingGifRunnable = object : Runnable {
            override fun run() {
                if (isGifLoading) {
                    mContext?.let {
                        Glide.with(it)
                            .load(imageResId)
                            .into(binding.ivCharacter)
                    }
                }
            }
        }
        gifHandler.postDelayed(pendingGifRunnable, 3000)
    }

    private fun stopLoadCharacterAnimation() {
        if (isGifLoading) {
            isGifLoading = false
            gifHandler.removeCallbacks(pendingGifRunnable)
        }
    }

    private fun stopTimer() {
        if (isTimerRunning) {
            isTimerRunning = false
            timerHandler.removeCallbacks(timerRunnable)
        }
    }

    private val clickTimeoutRunnable = Runnable {
        if (clickCount > 0) {
            processConsecutiveClicks(clickCount)
            clickCount = 0
        }
    }

    private fun processConsecutiveClicks(count: Int) {
        // 클릭 횟수가 1이면 연속 클릭이 아니므로 무시
        if (count <= 1) return

        // 화면 클릭 차단
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        // 서버에 데이터 전송
//        sendDataToServer(count)

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

//    private fun sendDataToServer(count: Int) {
//        // UI 업데이트
//        countText.text = "서버에 $count 회 전송 중..."
//
//        // 코루틴으로 네트워크 작업 처리
//        CoroutineScope(Dispatchers.IO).launch {
//            var successCount = 0
//
//            for (i in 1..count) {
//                try {
//                    val result = sendSingleRequest(i)
//                    if (result) successCount++
//
//                    // UI 스레드에서 진행 상황 업데이트
//                    withContext(Dispatchers.Main) {
//                        countText.text = "전송 중: $successCount / $count"
//                    }
//
//                    // 각 요청 사이에 약간의 지연
//                    delay(300)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//            // UI 스레드에서 작업 완료 처리
//            withContext(Dispatchers.Main) {
//                countText.text = "전송 완료: $successCount / $count"
//                overlay.visibility = View.GONE
//                Toast.makeText(
//                    this@MainActivity,
//                    "$count회 연속 클릭 데이터 전송 완료: $successCount/$count 성공",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }

    override fun onStop() {
        super.onStop()
        stopTimer()
        stopLoadCharacterAnimation()
    }
}