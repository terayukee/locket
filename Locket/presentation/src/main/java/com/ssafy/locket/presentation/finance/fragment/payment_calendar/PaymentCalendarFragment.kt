package com.ssafy.locket.presentation.finance.fragment.payment_calendar

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
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
import com.ssafy.locket.presentation.finance.viewmodel.SelectedDayPaymentsState
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

    private var scrollListener: RecyclerView.OnScrollListener? = null

    override fun onResume() {
        super.onResume()
        initUI()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        financeSharedViewModel.initYearMonth()

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

//        binding.calendar.monthScrollListener = { updateTitle() }
        binding.calendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendar.scrollToMonth(currentMonth)

        updateDayWeekColor()
        initObserver()

        initUI()
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

    private fun updateTitle() {
        val month = binding.calendar.findFirstVisibleMonth()?.yearMonth ?: return
        Log.d(TAG, "updateTitle: ${month.year} ${month.monthValue}")
        financeSharedViewModel.setYearMonth(month)
    }

    private fun dateClicked(date: LocalDate) {
        binding.calendar.notifyDateChanged(selectedDate) // 이전 선택값 해제

        selectedDate = date

        binding.calendar.notifyDateChanged(date) // 새로운 선택값
        selectedDayViewModel.setSelectedDay(date)
        selectedDayViewModel.setSelectedDayPayments(date.year, date.monthValue, date.dayOfMonth)
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

    private fun initUI() {
        dialog = PaymentCalendarBottomSheetFragment()

        var scrollDirection = "NONE"

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        Log.d(
                            TAG,
                            "onScrollStateChanged: Finished scrolling, direction=$scrollDirection"
                        )
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // 가로 스크롤 방향 감지
                if (dx != 0) {
                    scrollDirection = when {
                        dx > 0 -> "RIGHT_TO_LEFT" // 왼쪽으로 스크롤 (다음 달로)
                        dx < 0 -> "LEFT_TO_RIGHT" // 오른쪽으로 스크롤 (이전 달로)
                        else -> scrollDirection
                    }
                }

                Log.d(TAG, "onScrolled: scrollDirection ${scrollDirection}")
                // 스크롤 중에도 현재 보이는 월 확인 및 업데이트
                val prevMonth = financeSharedViewModel.selectedYearMonth.value
                val firstVisibleYearMonth =
                    binding.calendar.findFirstVisibleMonth()?.yearMonth ?: prevMonth


                val lastVisibleYearMonth =
                    binding.calendar.findLastVisibleMonth()?.yearMonth ?: prevMonth

                // 방향에 따라 적절한 월 선택
                if (firstVisibleYearMonth != null) {
                    val targetMonth = when (scrollDirection) {
                        "LEFT_TO_RIGHT" -> firstVisibleYearMonth
                        "RIGHT_TO_LEFT" -> {
                            lastVisibleYearMonth
                        }

                        else -> prevMonth
                    }

                    // 변경된 경우에만 업데이트
                    if (targetMonth != prevMonth) {
                        Log.d(
                            TAG,
                            "onScrolled: direction=$scrollDirection, changing month from $prevMonth to $targetMonth"
                        )
                        financeSharedViewModel.setYearMonth(targetMonth)
                    }
                }
            }
        }

        scrollListener?.let {
            binding.calendar.addOnScrollListener(it)
            Log.d(TAG, "initUI: scrollListener 추가")
        }


//        binding.calendar.setOnScrollChangeListener { _, _, _, _, _ ->
//            val prevMonth = financeSharedViewModel.selectedYearMonth.value
//            binding.calendar.fin
//            val firstVisibleYearMonth = binding.calendar.findFirstVisibleMonth()?.yearMonth
//            Log.d(TAG, "initUI: prevMonth ${prevMonth}  first ${firstVisibleYearMonth}")
//            if (firstVisibleYearMonth != null && firstVisibleYearMonth != prevMonth) {
//                financeSharedViewModel.setYearMonth(firstVisibleYearMonth)
//            }
//        }


//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                selectedDayViewModel.selectedDayPayments.collect { uiState ->
//                    when(uiState) {
//                        is SelectedDayPaymentsState.Success -> {
//                            Log.d(TAG, "initUI: success")
//                            if (uiState.paymentDailyHistory.list.isNotEmpty()) dialog.show(childFragmentManager, "payment")
//                        }
//                        is SelectedDayPaymentsState.Error -> {
//                            CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, uiState.message)
//                        }
//                        else -> Log.d(TAG, "initUI: SelectedDayPaymentsState else")
//                    }
//                }
//            }
//        }
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            financeSharedViewModel.selectedYearMonth.collectLatest {// 연월 선택
                binding.calendar.smoothScrollToMonth(YearMonth.of(it.year, it.monthValue))
                paymentHistoryViewModel.getMonthlyPaymentCalendar(it.year, it.monthValue)
//                currentMonth = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedDayViewModel.openDialog.collect { uiState ->
                    if (uiState is OpenDialogState.Opened && !dialog.isAdded) {
                        Log.d(TAG, "initUI: dialog is added")
                        dialog.show(childFragmentManager, "payment")
                        yield()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                paymentHistoryViewModel.monthlyPaymentCalendar.collect { uiState ->
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

    override fun onPause() {
        super.onPause()

        scrollListener?.let {
            binding.calendar.removeOnScrollListener(it)
            Log.d(TAG, "onPause: scrollListener 제거")
        }
        scrollListener = null
    }
}