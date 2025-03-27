package com.ssafy.locket.ui.payment

import android.os.Bundle
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentLogoutDialogBinding
import com.ssafy.locket.databinding.FragmentRecertifyDialogBinding
import com.ssafy.locket.ui.login.LoginActivity
import com.ssafy.locket.ui.payment.viewmodel.RecertifyViewModel

class RecertifyDialogFragment: DialogFragment() {
    private var _binding: FragmentRecertifyDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecertifyViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        _binding = FragmentRecertifyDialogBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
        // Optional: Customize dialog appearance
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        initEvent()
        return dialog
    }

    fun initEvent(){
        binding.btnPay.setOnClickListener {
            findNavController().navigate(R.id.paymentPasswordFragment)
            dismiss()
        }

        //인증하기 위한 과정
        binding.btnRecertify.setOnClickListener {
            viewModel.updateRecertify(1)
            findNavController().navigate(R.id.paymentPasswordFragment)
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // ✅ 메모리 누수 방지
    }
}