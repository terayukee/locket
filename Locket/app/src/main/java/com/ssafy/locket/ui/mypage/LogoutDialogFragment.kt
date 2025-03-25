package com.ssafy.locket.ui.mypage

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentLogoutDialogBinding

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
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            performLogout()
            dismiss()
        }

        // Optional: Customize dialog appearance
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    private fun performLogout() {
        // 로그아웃 로직
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // ✅ 메모리 누수 방지
    }
}