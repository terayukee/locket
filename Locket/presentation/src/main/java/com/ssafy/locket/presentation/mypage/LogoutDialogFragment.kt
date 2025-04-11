package com.ssafy.locket.presentation.mypage

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.kakao.sdk.user.UserApiClient
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.FragmentLogoutDialogBinding
import com.ssafy.locket.presentation.login.LoginActivity

class LogoutDialogFragment : DialogFragment() {
    private var _binding: FragmentLogoutDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        _binding = FragmentLogoutDialogBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)

        binding.btnLogout.setOnClickListener {
            performLogout()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    private fun performLogout() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e("Logout", "카카오 연결 해제 실패", error)
            } else {
                Log.i("Logout", "카카오 연결 해제 성공")

                if (isAdded && activity != null) {
                    val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                    requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    requireActivity().finishAffinity()

                    // 안전하게 다이얼로그 닫기
                    dialog?.window?.decorView?.post {
                        dismissAllowingStateLoss()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}