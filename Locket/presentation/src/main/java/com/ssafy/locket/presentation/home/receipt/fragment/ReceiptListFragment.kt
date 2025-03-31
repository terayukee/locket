package com.ssafy.locket.presentation.home.receipt.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.ssafy.locket.presentation.finance.adapter.PaymentRVAdapter
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.finance.Payment
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentReceiptListBinding
import com.ssafy.locket.presentation.home.receipt.viewmodel.FileTypeSelectionUiState
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptFileSelectionUiState
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptFileSelectionViewModel
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

private const val TAG = "ReceiptListFragment"
class ReceiptListFragment : BaseFragment<FragmentReceiptListBinding>(
    FragmentReceiptListBinding::bind,
    R.layout.fragment_receipt_list
) {
    private lateinit var paymentRVAdapter: PaymentRVAdapter
    private val receiptFileSelectionViewModel : ReceiptFileSelectionViewModel by activityViewModels()
    private val receiptViewModel : ReceiptViewModel by activityViewModels()
    private lateinit var file: File
    private lateinit var currentPhotoPath: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
        initAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            receiptFileSelectionViewModel.selectedType.collect { uiState ->
                if(uiState is FileTypeSelectionUiState.Image) {
                    imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else if(uiState is FileTypeSelectionUiState.Pdf) {
                    pdfPickerLauncher.launch("application/pdf")
                } else if(uiState is FileTypeSelectionUiState.Camera) {
                    imageCapture()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            receiptFileSelectionViewModel.receiptFileSelectionUiState.collect { uiState ->
                if(uiState is ReceiptFileSelectionUiState.Success) {
                    val uri = uiState.uri
                    Log.d(TAG, "onViewCreated: uri")
                    findNavController().navigate(R.id.action_receiptListFragment_to_receiptDetailFragment)

                    // TODO 서버로 던진 후에 아래 두 값 초기화 시키기
                    receiptFileSelectionViewModel.clearSelectedReceiptFile()
                    receiptFileSelectionViewModel.clearSelectedType()
                }
            }
        }

    }

    private fun imageCapture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        file = createImageFileName()
        val photoUri = FileProvider.getUriForFile(requireContext(), "com.ssafy.locket.fileprovider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        cameraLauncher.launch(intent)
    }

    private fun createImageFileName(): File {
        val timeStamp: String = SimpleDateFormat("MMdd_HHmm").format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "Locket_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun initAdapter() {
        paymentRVAdapter = PaymentRVAdapter("receipt")

        binding.rvReceipt.apply {
            adapter = paymentRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        paymentRVAdapter.itemClickListener = object : PaymentRVAdapter.ItemClickListener {
            override fun onClick(view: View, data: Payment, position: Int) {
                receiptViewModel.setSelectedPaymentReceipt(data)
                findNavController().navigate(R.id.receiptUploadDialog)
            }
        }

        val tmpList : List<Payment> = listOf(
            Payment(0, "쿠팡", "쇼핑", "내일배움카드", 3000, "2024.03.11"),
            Payment(1, "쿠팡", "쇼핑", "내일배움카드3", 8000, "2024.03.10")
        )
        paymentRVAdapter.submitList(tmpList)
    }

    fun initEvent(){
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { uri ->
            receiptFileSelectionViewModel.selectReceiptFile(uri)
        }
    }

    private val pdfPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uri ->
            receiptFileSelectionViewModel.selectReceiptFile(uri)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            receiptFileSelectionViewModel.selectReceiptFile(Uri.fromFile(file))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        receiptFileSelectionViewModel.clearSelectedType()
    }
}