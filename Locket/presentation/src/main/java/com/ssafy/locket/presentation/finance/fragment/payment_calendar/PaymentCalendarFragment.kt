package com.ssafy.locket.presentation.finance.fragment.payment_calendar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.CalendarDayBinding
import com.ssafy.locket.presentation.databinding.FragmentPaymentCalendarBinding
import com.ssafy.locket.presentation.finance.viewmodel.DailyPaymentState
import com.ssafy.locket.presentation.finance.viewmodel.FinanceSharedViewModel
import com.ssafy.locket.presentation.finance.viewmodel.PaymentCalendarState
import com.ssafy.locket.presentation.finance.viewmodel.PaymentHistoryState
import com.ssafy.locket.presentation.finance.viewmodel.PaymentHistoryViewModel
import com.ssafy.locket.presentation.finance.viewmodel.SelectedDayPaymentsState
import com.ssafy.locket.presentation.finance.viewmodel.SelectedDayViewModel
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import com.ssafy.locket.utils.CalendarUtils.displayText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private const val TAG = "PaymentCalendarFragment"
@AndroidEntryPoint
class PaymentCalendarFragment : BaseFragment<FragmentPaymentCalendarBinding>(
    FragmentPaymentCalendarBinding::bind,
    R.layout.fragment_payment_calendar
) {
    private var selectedDate = LocalDate.now()
    private var currentMonth = YearMonth.now()
    private val startMonth = YearMonth.of(2020, 1)
    private val endMonth = YearMonth.of(2030, 12)
    private val daysOfWeek = daysOfWeek(DayOfWeek.MONDAY)
    private val financeSharedViewModel: FinanceSharedViewModel by activityViewModels()
    private val paymentHistoryViewModel: PaymentHistoryViewModel by activityViewModels()
    private val selectedDayViewModel: SelectedDayViewModel by activityViewModels()
    private lateinit var dialog : PaymentCalendarBottomSheetFragment
    private val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val dayBinding = CalendarDayBinding.bind(view)
            val dayText = dayBinding.day
            val paymentText = dayBinding.payment

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        dateClicked(date = day.date)
                    }
                }
            }
        }

        binding.calendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(data.date, container.dayText, container.paymentText, data.position == DayPosition.MonthDate)
            }
            override fun create(view: View): DayViewContainer = DayViewContainer(view)
        }

        binding.calendar.monthScrollListener = { updateTitle() }
        binding.calendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendar.scrollToMonth(currentMonth)

        updateDayWeekColor()

        initUI()

    }

    private fun updateDayWeekColor() {
        for ((index, dayText) in daysOfWeek.withIndex()) {
            val dayLayout = binding.layoutDow.root.getChildAt(index)
            if (dayLayout != null){
                val textView: TextView? = dayLayout.findViewById(R.id.dayWeekText)
                if (textView != null) {
                    textView.text = dayText.displayText()
                }
            }
        }
    }

    private fun updateTitle() {
        val month = binding.calendar.findFirstVisibleMonth()?.yearMonth ?: return
        val year = month.year.toString() +"년"
        currentMonth = month
        financeSharedViewModel.setYearMonth(currentMonth)
    }

    private fun dateClicked(date: LocalDate) {
        binding.calendar.notifyDateChanged(selectedDate) // 이전 선택값 해제

        selectedDate = date

        binding.calendar.notifyDateChanged(date) // 새로운 선택값
        selectedDayViewModel.setSelectedDay(date)
        selectedDayViewModel.setSelectedDayPayments(date.year, date.monthValue, date.dayOfMonth)
    }

    private fun bindDate(date: LocalDate, dayText: TextView, paymentText: TextView, isSelectable: Boolean) {
        dayText.text = date.dayOfMonth.toString()
        val fonts = arrayOf(R.font.pretendard_regular, R.font.pretendard_bold)

        if (isSelectable) {
            when {
                date == selectedDate -> {
                    dayText.apply {
                        setTextColor(resources.getColor(R.color.primary))
                        typeface = ResourcesCompat.getFont(context, fonts[1])
                    }
                }
                else -> {
                    dayText.apply {
                        setTextColor(resources.getColor(R.color.text))
                        typeface = ResourcesCompat.getFont(context, fonts[0])
                    }
                }
            }
            paymentHistoryViewModel.monthlyPaymentCalendar.value.let { uiState ->
                if(uiState is PaymentCalendarState.Success) {
                    val element = uiState.paymentCalendar.dailySpending.find { it.date == date.format(format) }
                    if(element != null) {
                        paymentText.apply {
                            setText(getString(R.string.finance_calendar_payment,CommonUtils.makeComma(element.amount)))
                        }
                    }
                }
            }
        } else {
            dayText.setTextColor(resources.getColor(R.color.disabled))
        }
    }

    private fun initUI() {
        dialog = PaymentCalendarBottomSheetFragment()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                paymentHistoryViewModel.monthlyPaymentCalendar.collect { uiState ->
                    when(uiState) {
                        is PaymentCalendarState.Success -> {
                            uiState.paymentCalendar.dailySpending.map { // 월단위 결제내역 캘린더에 mapping
                                binding.calendar.notifyDateChanged(LocalDate.parse(it.date, format))
                            }
                        }
                        is PaymentCalendarState.Error -> {
                            Log.d(TAG, "initUI: Error calendar ${uiState.message}")
                            CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, uiState.message)
                        }
                        else -> Log.d(TAG, "initUI: Calendar Initial or Loading")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            financeSharedViewModel.selectedYearMonth.collect {// 연월 선택
                binding.calendar.smoothScrollToMonth(YearMonth.of(it.year, it.monthValue))
                paymentHistoryViewModel.getMonthlyPaymentCalendar(it.year, it.monthValue)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedDayViewModel.selectedDayPayments.collect { uiState ->
                    when(uiState) {
                        is SelectedDayPaymentsState.Success -> {
                            Log.d(TAG, "initUI: success")
                            if (uiState.paymentDailyHistory.list.isNotEmpty()) dialog.show(childFragmentManager, "payment")
                        }
                        is SelectedDayPaymentsState.Error -> {
                            CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, uiState.message)
                        }
                        else -> Log.d(TAG, "initUI: SelectedDayPaymentsState else")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(::dialog.isInitialized) dialog.dismiss()
    }
}