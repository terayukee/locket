package com.ssafy.locket.presentation.finance.fragment.payment_calendar

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
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
import com.ssafy.locket.presentation.finance.viewmodel.OpenDialogState
import com.ssafy.locket.presentation.finance.viewmodel.PaymentCalendarState
import com.ssafy.locket.presentation.finance.viewmodel.PaymentHistoryState
import com.ssafy.locket.presentation.finance.viewmodel.PaymentHistoryViewModel
import com.ssafy.locket.presentation.finance.viewmodel.SelectedDayState
import com.ssafy.locket.presentation.finance.viewmodel.SelectedDayViewModel
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import com.ssafy.locket.utils.CalendarUtils.displayText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.truncate

private const val TAG = "PaymentCalendarFragment"

@AndroidEntryPoint
class PaymentCalendarFragment : BaseFragment<FragmentPaymentCalendarBinding>(
    FragmentPaymentCalendarBinding::bind,
    R.layout.fragment_payment_calendar
) {
    private var selectedDate = LocalDate.now()
    private val today = LocalDate.now()
    private val startMonth = YearMonth.of(2020, 1)
    private val endMonth = YearMonth.of(today.year, today.monthValue)
    private val daysOfWeek = daysOfWeek(DayOfWeek.MONDAY)
    private val financeSharedViewModel: FinanceSharedViewModel by activityViewModels()
    private val paymentHistoryViewModel: PaymentHistoryViewModel by activityViewModels()
    private val selectedDayViewModel: SelectedDayViewModel by activityViewModels()
    private lateinit var dialog: PaymentCalendarBottomSheetFragment
    private val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        financeSharedViewModel.initYearMonthPayment()
        var downX = 0f
        var downY = 0f

        binding.calendar.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                    // false 반환 → 여기서는 이벤트를 소비하지 않음(달력 내부 로직으로 전달됨)
                    false
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = kotlin.math.abs(event.x - downX)
                    val dy = kotlin.math.abs(event.y - downY)
                    val threshold = ViewConfiguration.get(requireContext()).scaledTouchSlop

                    // 사용자가 손가락을 임계값 이상 움직였다면 = 스크롤 시도
                    if (dx > threshold || dy > threshold) {
                        // true 반환 → 이벤트 소비(스와이프/스크롤 동작 막기)
                        true
                    } else {
                        // 아직 크게 움직이지 않았으므로, 클릭(탭) 가능성 있음
                        false
                    }
                }

                // UP, CANCEL 등은 상황에 맞게 처리 (여기서는 기본적으로 false)
                else -> false
            }
        }

        val currentMonth = financeSharedViewModel.selectedYearMonth.value

        paymentHistoryViewModel.getMonthlyPaymentCalendar(
            currentMonth.year,
            currentMonth.monthValue
        )

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val dayBinding = CalendarDayBinding.bind(view)
            val dayText = dayBinding.day
            val paymentText = dayBinding.payment

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        Log.d(TAG, "onViewCreated: dateclicked")
                        dateClicked(date = day.date)
                    }
                }
            }
        }

        binding.calendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(
                    data.date,
                    container.dayText,
                    container.paymentText,
                    data.position == DayPosition.MonthDate
                )
            }

            override fun create(view: View): DayViewContainer = DayViewContainer(view)
        }

        binding.calendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendar.scrollToMonth(currentMonth)
        
        dialog = PaymentCalendarBottomSheetFragment()

        updateDayWeekColor()
        initObserver()
    }

    private fun updateDayWeekColor() {
        for ((index, dayText) in daysOfWeek.withIndex()) {
            val dayLayout = binding.layoutDow.root.getChildAt(index)
            if (dayLayout != null) {
                val textView: TextView? = dayLayout.findViewById(R.id.dayWeekText)
                if (textView != null) {
                    textView.text = dayText.displayText()
                }
            }
        }
    }

    private fun dateClicked(date: LocalDate) {
        if(selectedDayViewModel.selectedDay.value is SelectedDayState.Exist == false) {
            binding.calendar.notifyDateChanged(selectedDate) // 이전 선택값 해제

            selectedDate = date

            binding.calendar.notifyDateChanged(date) // 새로운 선택값
            selectedDayViewModel.setSelectedDay(date)
            selectedDayViewModel.setSelectedDayPayments(date.year, date.monthValue, date.dayOfMonth)
        }
    }

    private fun bindDate(
        date: LocalDate,
        dayText: TextView,
        paymentText: TextView,
        isSelectable: Boolean
    ) {
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
                if (uiState is PaymentCalendarState.Success) {
                    val element =
                        uiState.paymentCalendar.dailySpending.find { it.date == date.format(format) }
                    Log.d(TAG, "bindDate: element ${element?.date} ${element?.amount}")
                    if (element != null) {
                        paymentText.apply {
                            text = getString(
                                R.string.finance_calendar_payment,
                                CommonUtils.makeCommaDecimal(element.amount)
                            )
                        }
                    } else {
                        paymentText.setText(null)
                    }
                }
            }
        } else {
            dayText.setTextColor(resources.getColor(R.color.disabled))
            paymentText.setText(null)
        }
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            financeSharedViewModel.selectedYearMonth.collectLatest {// 연월 선택
                binding.calendar.scrollToMonth(YearMonth.of(it.year, it.monthValue))
                paymentHistoryViewModel.getMonthlyPaymentCalendar(it.year, it.monthValue)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedDayViewModel.selectedDay.collectLatest { uiState ->
                    if(uiState is SelectedDayState.Exist && !dialog.isAdded) {
                        dialog.show(childFragmentManager, "payment")
                        yield()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                paymentHistoryViewModel.monthlyPaymentCalendar.collectLatest { uiState ->
                    when (uiState) {
                        is PaymentCalendarState.Success -> {
                            uiState.paymentCalendar.dailySpending.map { // 월단위 결제내역 캘린더에 mapping
                                binding.calendar.notifyDateChanged(LocalDate.parse(it.date, format))
                            }
                        }

                        is PaymentCalendarState.Error -> {
                            Log.d(TAG, "initUI: Error calendar ${uiState.message}")
                            CommonUtils.showSingleLineCustomToast(
                                requireContext(),
                                ToastType.ERROR,
                                uiState.message
                            )
                        }

                        else -> Log.d(TAG, "initUI: Calendar Initial or Loading")
                    }
                }
            }
        }
    }
}