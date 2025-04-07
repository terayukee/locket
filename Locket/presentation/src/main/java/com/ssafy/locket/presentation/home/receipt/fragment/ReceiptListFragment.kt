package com.ssafy.locket.presentation.home.receipt.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.home.receipt.Receipt
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentReceiptListBinding
import com.ssafy.locket.presentation.home.receipt.adapter.AvailableReceiptAdapter
import com.ssafy.locket.presentation.home.receipt.viewmodel.FileTypeSelectionUiState
import com.ssafy.locket.presentation.home.receipt.viewmodel.NavigateToDetailEvent
import com.ssafy.locket.presentation.home.receipt.viewmodel.PaymentReceiptListState
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptDetailState
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptFileSelectionUiState
import com.ssafy.locket.presentation.home.receipt.viewmodel.ReceiptFileSelectionViewModel
import com.ssafy.locket.presentation.home.receipt.viewmodel.SelectedReceiptState
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

private const val TAG = "ReceiptListFragment"
class ReceiptListFragment : BaseFragment<FragmentReceiptListBinding>(
    FragmentReceiptListBinding::bind,
    R.layout.fragment_receipt_list
) {
    private lateinit var availableReceiptAdapter: AvailableReceiptAdapter
    private val receiptFileSelectionViewModel : ReceiptFileSelectionViewModel by activityViewModels()
    private lateinit var file: File
    private lateinit var currentPhotoPath: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
        initAdapter()

        receiptFileSelectionViewModel.getAllAvailableReceipts()

        viewLifecycleOwner.lifecycleScope.launch {
            receiptFileSelectionViewModel.receiptList.collect { uiState ->
                if(uiState is PaymentReceiptListState.Success) {
                    availableReceiptAdapter.submitList(uiState.paymentReceiptList)
                }
            }
        }

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

                    val transactionId = if(receiptFileSelectionViewModel.selectedReceipt.value is SelectedReceiptState.Success) (receiptFileSelectionViewModel.selectedReceipt.value as? SelectedReceiptState.Success)?.transactionId else null

                    when(receiptFileSelectionViewModel.selectedType.value) {
                        is FileTypeSelectionUiState.Image, FileTypeSelectionUiState.Camera -> {
                            transactionId?.let {
                                receiptFileSelectionViewModel.processReceipt("image", uri, it)
                            }
                        }
                        is FileTypeSelectionUiState.Pdf -> {
                            transactionId?.let {
                                receiptFileSelectionViewModel.processReceipt("pdf", uri, it)
                            }
                        }
                        else -> {}
                    }

                    // TODO 서버로 던진 후에 아래 두 값 초기화 시키기
                    receiptFileSelectionViewModel.clearSelectedReceiptFile()
                    receiptFileSelectionViewModel.clearSelectedType()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            receiptFileSelectionViewModel.navigationEvent.collect { uiState ->
                when(uiState) {
                    is NavigateToDetailEvent.Loading -> {
                        Log.d(TAG, "onViewCreated: loading")
                        binding.progressBar.visibility = View.VISIBLE
                        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                    is NavigateToDetailEvent.Move -> {
                        binding.progressBar.visibility = View.GONE
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    findNavController().navigate(R.id.action_receiptListFragment_to_receiptDetailFragment)
                    }
                    is NavigateToDetailEvent.Initial -> {
                        Log.d(TAG, "onViewCreated: error")
                        CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, "오류가 발생했습니다. 잠시후 다시 시도해주세요")
                    }
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
        availableReceiptAdapter = AvailableReceiptAdapter()

        binding.rvReceipt.apply {
            adapter = availableReceiptAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        availableReceiptAdapter.itemClickListener = object : AvailableReceiptAdapter.ItemClickListener {
            override fun onClick(view: View, data: Receipt, position: Int) {
                receiptFileSelectionViewModel.selectReceipt(data.transactionId)
                findNavController().navigate(R.id.receiptUploadDialog)
            }
        }
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

    override fun onResume() {
        super.onResume()
        receiptFileSelectionViewModel.clearSelectedType()
    }
}