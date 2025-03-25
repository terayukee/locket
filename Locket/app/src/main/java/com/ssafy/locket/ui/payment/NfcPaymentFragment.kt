package com.ssafy.locket.ui.payment

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

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // 애니메이션 시작 시 배경색을 검정색으로 유지
                requireActivity().window.decorView.setBackgroundColor(Color.BLACK)
            }

            override fun onAnimationEnd(animation: Animation?) {
                // 뒤로가기 후 애니메이션 끝나면 배경색을 원래대로 돌려놓기
                requireActivity().window.decorView.setBackgroundColor(Color.WHITE)  // 원래 배경색으로 되돌리기
                findNavController().popBackStack()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        cardView.startAnimation(animation)
    }
}