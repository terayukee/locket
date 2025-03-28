package com.ssafy.locket.presentation.home.receipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.DialogReceiptUploadBinding

class ReceiptUploadDialog: DialogFragment() {
    private var _binding: DialogReceiptUploadBinding? = null
    private val binding : DialogReceiptUploadBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogReceiptUploadBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDialog()

        binding.btnUploadImg.setOnClickListener {
            // TODO 갤러리 이미지 열기
            findNavController().navigate(R.id.action_receiptUploadDialog_to_receiptDetailFragment)
        }

        binding.btnUploadPdf.setOnClickListener {
            // TODO 파일 열기
        }

        binding.btnCamera.setOnClickListener {
            // TODO 카메라 촬영 열기
        }

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setDialog() {
        val displayMetrics = resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels

        val params = dialog?.window?.attributes
        params?.width = (widthPixels * 0.8).toInt()
        params?.height = ((widthPixels * 0.8).toInt() * 1.06).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}