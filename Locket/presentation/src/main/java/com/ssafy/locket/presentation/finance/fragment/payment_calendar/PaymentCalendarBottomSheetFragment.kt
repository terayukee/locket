package com.ssafy.locket.presentation.finance.fragment.payment_calendar

import com.ssafy.locket.presentation.finance.adapter.PaymentRVAdapter
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.common.view.MainActivity
import com.ssafy.locket.presentation.databinding.FragmentPaymentListBottomSheetBinding
import com.ssafy.locket.presentation.finance.adapter.DailyPaymentRVAdapter
import com.ssafy.locket.presentation.finance.viewmodel.SelectedDayPaymentsState
import com.ssafy.locket.presentation.finance.viewmodel.SelectedDayState
import com.ssafy.locket.presentation.finance.viewmodel.SelectedDayViewModel
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import com.ssafy.locket.utils.CalendarUtils.displayText
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.ViewComponentManager
import kotlinx.coroutines.launch

private const val TAG = "PaymentCalendarBottomSh"
@AndroidEntryPoint
class PaymentCalendarBottomSheetFragment : BottomSheetDialogFragment() {

    private var mContext : Context? = null
    private var _binding : FragmentPaymentListBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var dailyPaymentRVAdapter: DailyPaymentRVAdapter
    private val selectedDayViewModel: SelectedDayViewModel by activityViewModels()

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
    }

    private fun init(){
        dailyPaymentRVAdapter = DailyPaymentRVAdapter()

        binding.rvPayment.apply {
            adapter = dailyPaymentRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.btnClose.setOnClickListener {
            selectedDayViewModel.clearSelectedDay()
            selectedDayViewModel.clearSelectedDayPayments()
            dialog?.dismiss()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            selectedDayViewModel.selectedDay.collect { uiState ->
                when(uiState) {
                    is SelectedDayState.Selected -> {
                        binding.tvDate.text = String.format(getString(R.string.finance_calendar_bottom_sheet_date),uiState.day.dayOfMonth.toString(),uiState.day.dayOfWeek.displayText())
                    }
                    is SelectedDayState.Initial -> {
                        Log.d(TAG, "onViewCreated: initial")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            selectedDayViewModel.selectedDayPayments.collect { uiState ->
                when(uiState) {
                    is SelectedDayPaymentsState.Success -> {
                        binding.tvCount.text = String.format(getString(R.string.finance_calendar_bottom_sheet_count), uiState.paymentDailyHistory.list.size)
                        dailyPaymentRVAdapter.submitList(uiState.paymentDailyHistory.list)
                    }
                    is SelectedDayPaymentsState.Error -> {
                        Log.d(TAG, "initUI: Error calendar ${uiState.message}")
                        CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, uiState.message)
                    }
                    else -> Log.d(TAG, "init: selectedDayViewModel.selectedDayPayments else")
                }
            }
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
        (getActivityContext(requireContext()) as MainActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
    override fun onPause() {
        super.onPause()
        selectedDayViewModel.clearSelectedDay()
        selectedDayViewModel.clearSelectedDayPayments()
        dialog?.dismiss()
    }

    fun getActivityContext(context: Context): Context {
        return if (context is ViewComponentManager.FragmentContextWrapper) {
            context.baseContext
        } else {
            context
        }
    }
}