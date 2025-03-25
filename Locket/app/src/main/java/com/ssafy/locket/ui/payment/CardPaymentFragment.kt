package com.ssafy.locket.ui.payment

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.data.remote.dto.Card
import com.ssafy.locket.databinding.FragmentCardPaymentBinding
import com.ssafy.locket.ui.payment.Adapter.CardAdapter

class CardPaymentFragment : BaseFragment<FragmentCardPaymentBinding>(
    FragmentCardPaymentBinding::bind,
    R.layout.fragment_card_payment
) {
    private lateinit var cardAdapter: CardAdapter
    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    //지문 관련
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null

    val cards = listOf(
        Card(R.drawable.ic_payment_card_img, "국민행복 삼성카드 V2"),
        Card(R.drawable.ic_payment_card_img, "Another Card"),
        Card(R.drawable.ic_payment_card_img, "Third Card"),
        Card(R.drawable.ic_payment_card_img, "국민행복 삼성카드 V2"),
        Card(R.drawable.ic_payment_card_img, "Another Card"),
        Card(R.drawable.ic_payment_card_img, "Third Card"),
        Card(R.drawable.ic_payment_card_img, "국민행복 삼성카드 V2"),
        Card(R.drawable.ic_payment_card_img, "Another Card"),
        Card(R.drawable.ic_payment_card_img, "Third Card"),
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialAdapter()
        initEvent()
    }

    fun initialAdapter(){
        cardAdapter = CardAdapter(cards)
        binding.viewpager.adapter = cardAdapter
        setupDotIndicator(cards.size)
    }


    fun initEvent(){
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                binding.tvCardName.text = cards[position].name
            }
        })
        binding.ivFingerprint.setOnClickListener {
            setupBiometricPrompt()
            if (biometricPrompt != null && promptInfo != null) {
                biometricPrompt?.authenticate(promptInfo!!)
            } else {
                showToast("지문 인증이 준비되지 않았습니다.")
            }
        }
        binding.btnPassword.setOnClickListener {
            findNavController().navigate(R.id.action_cardPaymentFragment_to_paymentPasswordFragment)
        }
    }

    private fun setupDotIndicator(count: Int) {
        val MAX_VISIBLE_DOTS = 5

        val maxDotSize = 12.dp
        val minDotSize = 10.dp
        val dotSize = (maxDotSize * (1f / (count.coerceAtMost(MAX_VISIBLE_DOTS)))).coerceAtLeast(minDotSize.toFloat())

        val dotContainer = binding.dotIndicator
        dotContainer.removeAllViews()

        repeat(count) { index ->
            val dot = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(dotSize.toInt(), dotSize.toInt()).apply {
                    setMargins(4.dp, 4.dp, 4.dp, 4.dp)
                }
                setBackgroundResource(R.drawable.ic_payment_indicater)
            }
            dotContainer.addView(dot)
        }

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                if (count > MAX_VISIBLE_DOTS) {
                    val scrollView = binding.dotIndicatorScroll
                    val selectedDot = dotContainer.getChildAt(position)

                    // Calculate scroll position to center the selected dot
                    val dotCenterX = selectedDot.left + selectedDot.width / 2
                    val scrollViewCenterX = scrollView.width / 2

                    // Smooth scroll the dot indicator
                    scrollView.smoothScrollTo(
                        dotCenterX - scrollViewCenterX,
                        0
                    )
                }
                binding.tvCardName.text = cards[position].name
            }
        })
        updateDots(0)
    }

    private fun updateDots(selectedPosition: Int) {
        val dotContainer = binding.dotIndicator
        for (i in 0 until dotContainer.childCount) {
            val dot = dotContainer.getChildAt(i)
            dot.alpha = if (i == selectedPosition) 1f else 0.2f
        }
    }

    private fun setupBiometricPrompt() {
        biometricPrompt = BiometricPrompt(
            requireActivity(), // requireActivity()를 사용하여 Activity context를 전달
            ContextCompat.getMainExecutor(requireContext()), // MainExecutor 사용
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // 인증 성공 시 처리할 로직
                    showToast("지문 인증 성공")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // 인증 실패 시 처리할 로직
                    showToast("지문 인증 실패")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // 인증 중 에러 처리
                    showToast("인증 오류: $errString")
                }
            })

        // 인증 팝업 화면 정보 설정
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("지문 인증")
            .setSubtitle("지문을 등록한 후 인증하세요.")
            .setNegativeButtonText("취소")
            .build()
    }
}