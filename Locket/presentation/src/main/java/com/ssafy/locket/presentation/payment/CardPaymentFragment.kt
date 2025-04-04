package com.ssafy.locket.presentation.payment

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.nfc.NfcAdapter
import android.os.Bundle
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.locket.ui.payment.viewmodel.RecertifyViewModel
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey
import com.ssafy.locket.model.graph.Card
import com.ssafy.locket.model.payment.PaymentCard
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentCardPaymentBinding
import com.ssafy.locket.presentation.payment.adapter.CardAdapter
import com.ssafy.locket.presentation.payment.viewmodel.CardPaymentViewModel
import com.ssafy.locket.presentation.payment.viewmodel.PaymentCardState
import com.ssafy.locket.presentation.payment.viewmodel.SelectedPaymentCardViewModel
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import com.ssafy.locket.ui.payment.RecertifyDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "CardPaymentFragment"
@AndroidEntryPoint
class CardPaymentFragment : BaseFragment<FragmentCardPaymentBinding>(
    FragmentCardPaymentBinding::bind,
    R.layout.fragment_card_payment
) {
    private lateinit var cardAdapter: CardAdapter
    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    //지문 관련 이벤트 처리때문에 viewModel작성
    private val viewModel: RecertifyViewModel by activityViewModels()
    private val cardPaymentViewModel: CardPaymentViewModel by viewModels()
    private val selectedPaymentCardViewModel: SelectedPaymentCardViewModel by activityViewModels()

    //스크롤 가능
    private var selectedPosition = -1
    private var lastScrollX = 0
    //뒤로 가기 이벤트
    private var backPressedTime: Long = 0
    //버튼 클릭으로 이벤트 지정
    private var btnClick = 0

    var cards = listOf<PaymentCard>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initState()
        initialView()
        initialAdapter()
        initEvent()
        backEvent()
        getCardView(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 현재 선택된 페이지 저장
        outState.putInt("selectedPosition", selectedPosition)
        // ScrollView의 스크롤 위치 저장
        outState.putInt("scrollPosition", binding.dotIndicatorScroll.scrollX)
    }

    fun getCardView(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            selectedPosition = it.getInt("selectedPosition", 0)
            lastScrollX = it.getInt("scrollPosition", 0)
            binding.viewpager.setCurrentItem(selectedPosition, false) // 애니메이션 없이 복원
            binding.dotIndicator.scrollTo(lastScrollX, 0) // ScrollView 복원
        }
        if(selectedPosition >= 0) scrollToDotAtPosition(selectedPosition)
    }

    fun initState() {
        viewLifecycleOwner.lifecycleScope.launch {
            cardPaymentViewModel.paymentCardList.collect { uiState ->
                if (uiState is PaymentCardState.Success) {
                    cards = uiState.paymentCardList.cards
                    cardAdapter.setCards(cards)
                    setupDotIndicator(cards.size)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            cardPaymentViewModel.isFingerprintRegistered.collect {
                if(it) initBiometrics()
                else CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.DEFAULT, "회원가입 시 지문이 등록되지 않았습니다")
            }
        }
    }

    fun initialAdapter() {
        cardAdapter = CardAdapter(cards)
        binding.viewpager.adapter = cardAdapter
    }

    fun backEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2000) {
                    requireActivity().finish() // 액티비티 종료
                } else {
                    backPressedTime = System.currentTimeMillis()
                    showToast("한 번 더 누르면 종료됩니다.")
                }
            }
        })
    }

    fun initialView(){
        cardPaymentViewModel.getAllPaymentCards()
        requireActivity().window.decorView.setBackgroundColor(Color.WHITE)
    }

    fun initEvent(){
        viewModel.updateRecertify(0)
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                binding.tvCardName.text = cards[position].cardName
                Log.d(TAG, "onPageSelected: card changed in initEvent ${cards[position].cardName}")
            }
        })
        binding.ivFingerprint.setOnClickListener {
            btnClick = 1
            selectedPaymentCardViewModel.selectPaymentCard(cards[selectedPosition])
            checkNFCEnabled()
        }
        binding.btnPassword.setOnClickListener {
            btnClick = 2
            selectedPaymentCardViewModel.selectPaymentCard(cards[selectedPosition])
            checkNFCEnabled()
        }
    }

    private fun scrollToDotAtPosition(position: Int) {
        val dotContainer = binding.dotIndicator
        val dot = dotContainer.getChildAt(position)

        val dotCenterX = dot.left + dot.width / 2
        val scrollViewCenterX = binding.dotIndicatorScroll.width / 2

        val scrollX = dotCenterX - scrollViewCenterX
        binding.dotIndicatorScroll.smoothScrollTo(scrollX, 0)
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
                selectedPosition = position
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
                binding.tvCardName.text = cards[position].cardName
                Log.d(TAG, "onPageSelected: card changed in registerOnPageChangeCallback ${cards[position].cardName}")
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

    private fun checkNFCEnabled() {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        if (nfcAdapter == null) {
            showToast("이 기기는 NFC를 지원하지 않습니다.")
            return
        }
        if (!nfcAdapter.isEnabled) {
            AlertDialog.Builder(requireContext())
                .setTitle("NFC가 꺼져 있음")
                .setMessage("NFC를 활성화해야 결제를 진행할 수 있습니다. NFC 설정을 활성화하기 전까지 결제가 불가능합니다.")
                .setPositiveButton("설정으로 이동") { _, _ ->
                    startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                }
                .setNegativeButton("취소", null)
                .show()
        }
        else{
            if(btnClick==1){
//                initBiometrics()
                cardPaymentViewModel.checkFingerprintRegistered()
            }
            else if(btnClick==2){
                requireActivity().window.decorView.setBackgroundColor(Color.BLACK)
                findNavController().navigate(R.id.action_cardPaymentFragment_to_paymentPasswordFragment)
            }
        }
    }
    private fun initBiometrics() {
        val biometricManager = BiometricManager.from(requireContext())

        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                if (isFingerprintChanged()) {
                    //지문 변경되었으니 새로 등록하세요 먼저 간편비밀 번호 확인
                    showToast("지문이 변경되었습니다. 간편비밀번호으로 본인을 인증하세요.")
                    val dialogFragment = RecertifyDialogFragment()
                    dialogFragment.show(parentFragmentManager, "recertify_dialog")
                } else {
                    authenticateWithBiometrics()
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                showToast("이 장치에서는 생체 인식이 지원되지 않습니다.")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                showToast("생체 인식 하드웨어가 사용 불가능합니다.")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                showToast("생체 인식 정보가 등록되지 않았습니다.")
            }
            else -> {
                showToast("생체 인식 인증 실패")
            }
        }
    }

    // 📌 1. Keystore에서 SecretKey 가져오기
    private fun getSecretKey(): SecretKey? {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        return keyStore.getKey("biometric_key", null) as? SecretKey
    }

    // 📌 2. 지문이 변경되었는지 확인
    private fun isFingerprintChanged(): Boolean {
        return try {
            val secretKey = getSecretKey() ?: throw Exception("SecretKey is null")

            // 기존 SecretKey로 암호화 테스트
            val cipher = Cipher.getInstance(
                "${KeyProperties.KEY_ALGORITHM_AES}/" +
                        "${KeyProperties.BLOCK_MODE_CBC}/" +
                        KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
            cipher.init(Cipher.ENCRYPT_MODE, secretKey) // 기존 키로 암호화 가능 여부 확인
            false // 암호화 성공 → 기존 지문 유지됨
        } catch (e: Exception) {
            Log.e("Biometric", "지문 변경 감지됨!", e)
            //deleteSecretKey() // 기존 키 삭제
            //generateSecretKey() // 새 키 생성
            true // 암호화 실패 → 지문 변경됨
        }
    }

    //지문 검사 로직
    private fun authenticateWithBiometrics() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    showToast("지문 인증 성공!")
                    findNavController().navigate(R.id.action_cardPaymentFragment_to_nfcPaymentFragment)
                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showToast("지문 인증 실패. 다시 시도해주세요.")
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showToast("지문 인증 오류: $errString")
                }
            }
        )
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("지문 인증")
            .setSubtitle("결제를 위해 지문을 인증해주세요.")
            .setNegativeButtonText("취소")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

}