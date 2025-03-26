package com.ssafy.locket.presentation.finance.fragments.payment_calendar

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentPaymentListBottomSheetBinding
import com.ssafy.locket.presentation.home.receipt.adapter.ReceiptRVAdapter
import java.time.LocalDate


class PaymentCalendarBottomSheetFragment : BottomSheetDialogFragment() {

    private var mContext : Context? = null
    private var _binding : FragmentPaymentListBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var receiptRVAdapter: ReceiptRVAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentListBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        var date = LocalDate.now() // 임시 데이터
        // TODO 선택된 날짜와 데이터 전달 받아야 함

        binding.tvDate.text = String.format(getString(R.string.finance_calendar_bottom_sheet_date),date.dayOfMonth.toString(),"화")

        binding.tvCount.text = String.format(getString(R.string.finance_calendar_bottom_sheet_count), 2)
        
    }

    private fun init(){
        receiptRVAdapter = ReceiptRVAdapter("payment")

        binding.rvPayment.apply {
            adapter = receiptRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.btnClose.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(mContext!!, R.style.CustomDialog)
        dialog.window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.7f)
        }
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
            dialog.setCanceledOnTouchOutside(true)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        behavior.peekHeight = getBottomSheetDialogDefaultHeight()
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheet.layoutParams = bottomSheet.layoutParams
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return (getWindowHeight() * 0.6019).toInt()
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
    }
}