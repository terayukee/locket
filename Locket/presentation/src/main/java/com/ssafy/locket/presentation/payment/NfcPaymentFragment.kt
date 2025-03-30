package com.ssafy.locket.presentation.payment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentNfcPaymentBinding

class NfcPaymentFragment : BaseFragment<FragmentNfcPaymentBinding>(
    FragmentNfcPaymentBinding::bind,
    R.layout.fragment_nfc_payment
) {
    private val handler = Handler(Looper.getMainLooper())
    private var timeRemaining = 30  // 30초 설정
    private lateinit var vibrator: Vibrator
    private var isVibrating = false

    val vibrationPattern = longArrayOf(100, 200, 100, 200)
    private val vibrationAmplitude = intArrayOf(
        VibrationEffect.DEFAULT_AMPLITUDE,
        0,
        VibrationEffect.DEFAULT_AMPLITUDE,
        0
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize vibrator
        vibrator = ContextCompat.getSystemService(requireContext(), Vibrator::class.java)!!

        applyCardRotationAnimation()
        startTimer()
        startContinuousVibration()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            stopVibration()
            applyCardRotationExitAnimation()
            handler.removeCallbacksAndMessages(null)
        }
    }

    private fun startContinuousVibration() {
        isVibrating = true
        val vibrationRunnable = object : Runnable {
            @SuppressLint("NewApi")
            override fun run() {
                if (isVibrating) {
                    val vibrationEffect = VibrationEffect.createWaveform(
                        vibrationPattern,
                        vibrationAmplitude,
                        -1  // Repeat indefinitely
                    )
                    vibrator.vibrate(vibrationEffect)

                    handler.postDelayed(this, 800) // Schedule the next vibration cycle
                }
            }
        }
        handler.post(vibrationRunnable)  // Start vibration immediately
    }

    private fun stopVibration() {
        isVibrating = false
        vibrator.cancel()
        handler.removeCallbacksAndMessages(null)
    }

    private fun startTimer() {
        val timerRunnable = object : Runnable {
            override fun run() {
                if (timeRemaining >= 0) {
                    binding.tvTimer.text = timeRemaining.toString()  // 남은 시간 업데이트
                    timeRemaining--
                    handler.postDelayed(this, 1000)  // 1초 후 다시 실행
                } else {
                    stopVibration()
                    applyCardRotationExitAnimation()
                }
            }
        }
        handler.postDelayed(timerRunnable, 1000)  // 1초 후 시작
    }

    private fun applyCardRotationAnimation() {
        val cardView = binding.ivCard
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.card_flip_enter)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                binding.tvTitle.visibility = View.VISIBLE
                binding.tvInstruction.visibility = View.VISIBLE
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        cardView.startAnimation(animation)
    }

    private fun applyCardRotationExitAnimation() {
        val cardView = binding.ivCard
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.card_flip_out)

        // 애니메이션 시작 전에 배경색을 검정색으로 설정
        requireActivity().window.decorView.setBackgroundColor(Color.BLACK)

        val colorAnimator = ObjectAnimator.ofArgb(
            requireActivity().window.decorView,
            "backgroundColor",
            Color.BLACK, // 시작 색
            Color.WHITE // 종료 색
        )
        colorAnimator.duration = 500 // 애니메이션 시간 설정
        colorAnimator.start()

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // 애니메이션 시작 시 배경색을 검정색으로 유지
                requireActivity().window.decorView.setBackgroundColor(Color.BLACK)
            }
            override fun onAnimationEnd(animation: Animation?) {
                findNavController().popBackStack()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        cardView.startAnimation(animation)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVibration()
    }
}