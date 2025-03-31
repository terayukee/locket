package com.ssafy.locket.presentation.home.receipt

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.DialogReceiptUploadBinding
import com.ssafy.locket.presentation.home.receipt.viewmodel.FileTypeSelectionUiState
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptFileSelectionUiState
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptFileSelectionViewModel
import kotlinx.coroutines.launch

private const val TAG = "ReceiptUploadDialog"
class ReceiptUploadDialog: DialogFragment() {
    private var _binding: DialogReceiptUploadBinding? = null
    private val binding : DialogReceiptUploadBinding
        get() = _binding!!
    private val receiptFileSelectionViewModel : ReceiptFileSelectionViewModel by activityViewModels()

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
            receiptFileSelectionViewModel.selectType(FileTypeSelectionUiState.Image)
            dismiss()
        }

        binding.btnUploadPdf.setOnClickListener {
            receiptFileSelectionViewModel.selectType(FileTypeSelectionUiState.Pdf)
            dismiss()
        }

        binding.btnCamera.setOnClickListener {
            receiptFileSelectionViewModel.selectType(FileTypeSelectionUiState.Camera)
            dismiss()
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
        dialog?.dismiss()
    }

}