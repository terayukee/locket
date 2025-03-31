package com.ssafy.locket.presentation.home.receipt

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.locket.PermissionChecker
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.databinding.DialogReceiptUploadBinding
import com.ssafy.locket.presentation.home.receipt.viewmodel.FileTypeSelectionUiState
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptFileSelectionViewModel

private const val TAG = "ReceiptUploadDialog"
class ReceiptUploadDialog: DialogFragment() {
    private var _binding: DialogReceiptUploadBinding? = null
    private val binding : DialogReceiptUploadBinding
        get() = _binding!!
    private val receiptFileSelectionViewModel : ReceiptFileSelectionViewModel by activityViewModels()
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
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
            checkPermission()
            dismiss()
        }

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private val checker = PermissionChecker(this)

    private val runtimePermission =
        arrayOf(Manifest.permission.CAMERA)

    private fun checkPermission() {
//        Log.d(TAG, "checkPermission: checkPermission called")
        if (!checker.checkPermission(mContext, runtimePermission)) {
//            Log.d(TAG, "checkPermission: checkPermission not permitted")
            checker.requestPermissionLauncher.launch(runtimePermission)
        } else {
//            Log.d(TAG, "checkPermission: checkPermission permitted")
            receiptFileSelectionViewModel.selectType(FileTypeSelectionUiState.Camera)
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