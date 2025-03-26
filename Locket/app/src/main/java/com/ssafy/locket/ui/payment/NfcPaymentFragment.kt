package com.ssafy.locket.ui.payment

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentNfcPaymentBinding

class NfcPaymentFragment : BaseFragment<FragmentNfcPaymentBinding>(
    FragmentNfcPaymentBinding::bind,
    R.layout.fragment_nfc_payment
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyCardRotationAnimation()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            applyCardRotationExitAnimation()
        }
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
}