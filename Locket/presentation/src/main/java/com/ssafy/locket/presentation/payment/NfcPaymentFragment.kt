package com.ssafy.locket.presentation.payment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcF
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.common.view.MainActivity
import com.ssafy.locket.presentation.databinding.FragmentNfcPaymentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MainActivity_NFC"
class NfcPaymentFragment : BaseFragment<FragmentNfcPaymentBinding>(
    FragmentNfcPaymentBinding::bind,
    R.layout.fragment_nfc_payment
) {
    private var timeRemaining = 30  // 30초 설정
    private var isVibrating = false

    private lateinit var vibrator: Vibrator
    private val vibrationPattern = longArrayOf(100, 200, 100, 200)
    private val vibrationAmplitude = intArrayOf(
        VibrationEffect.DEFAULT_AMPLITUDE,
        0,
        VibrationEffect.DEFAULT_AMPLITUDE,
        0
    )

    //nfc어댑터
    private lateinit var nfcAdapter: NfcAdapter

    override fun onResume() {
        super.onResume()
        startNfcReader()
    }

    override fun onPause() {
        super.onPause()
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(requireActivity())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVibration()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        }
    }

    private fun startTimer() {
        CoroutineScope(Dispatchers.Main).launch {
            while (timeRemaining >= 0) {
                binding.tvTimer.text = timeRemaining.toString()  // 남은 시간 업데이트
                delay(1000)  // 1초 대기
                timeRemaining--
            }
            stopVibration()
            applyCardRotationExitAnimation()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startContinuousVibration() {
        isVibrating = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isVibrating) {
                val vibrationEffect = VibrationEffect.createWaveform(
                    vibrationPattern,
                    vibrationAmplitude,
                    -1  // Repeat indefinitely
                )
                vibrator.vibrate(vibrationEffect)
                delay(800)  // 800ms 후 반복
            }
        }
    }

    private fun stopVibration() {
        isVibrating = false
        vibrator.cancel()
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

    private fun startNfcReader() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        if (nfcAdapter == null) {
            Log.e(TAG, "NFC를 지원하지 않는 기기입니다.")
            return
        }
        if (!nfcAdapter.isEnabled) {
            return
        }

        val pendingIntent = PendingIntent.getActivity(
            requireContext(), 0,
            Intent(requireContext(), MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )

        val ndef = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val intentFiltersArray = arrayOf(ndef)
        nfcAdapter.enableForegroundDispatch(requireActivity(), pendingIntent, intentFiltersArray, null)
    }

    fun handleNfcTag(intent: Intent) {
        Log.d(TAG, "Fragment에서 태그 ID 처리:")
        val action = intent.action

        // NFC 태그가 발견되었을 때 처리
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action || action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            tag?.let {
                // NFC 태그에서 ID 가져오기
                val tagId = tag.id.joinToString(":") { String.format("%02X", it) }
                Log.d(TAG, "태그 ID: $tagId")
                val ndefMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

                // NDEF 메시지가 있을 경우 처리
                if (ndefMessages != null) {
                    for (message in ndefMessages) {
                        val ndefMessage = message as NdefMessage
                        for (record in ndefMessage.records) {
                            // NDEF 레코드에서 데이터 읽기
                            val payload = record.payload
                            val text = String(payload, charset("UTF-8"))
                            Log.d(TAG, "NDEF 데이터: $text")
                        }
                    }
                } else {
                    Log.e(TAG, "NDEF 메시지가 없습니다.")
                }
            }
        }
    }
}