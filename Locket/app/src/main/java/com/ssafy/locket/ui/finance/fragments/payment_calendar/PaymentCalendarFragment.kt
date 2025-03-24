package com.ssafy.locket.ui.finance.fragments.payment_calendar

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.locket.CommonUtils
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.CalendarDayBinding
import com.ssafy.locket.databinding.FragmentPaymentCalendarBinding
import com.ssafy.locket.utils.CalendarUtils.displayText
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class PaymentCalendarFragment : BaseFragment<FragmentPaymentCalendarBinding>(
    FragmentPaymentCalendarBinding::bind,
    R.layout.fragment_payment_calendar
) {
    private var selectedDate = LocalDate.now()
    private var currentMonth = YearMonth.now()
    private val startMonth = YearMonth.of(2015, 1) // 2024년 1월부터 제공
    private val endMonth = YearMonth.of(2040, 12) // 2024년 1월부터 제공
    private val daysOfWeek = daysOfWeek(DayOfWeek.MONDAY)

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
    }

    private fun updateDayWeekColor() {
        for ((index, dayText) in daysOfWeek.withIndex()) {
            val dayLayout = binding.layoutDow.root.getChildAt(index)
            if (dayLayout!=null){
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
//        viewModel.getMonthSchedules(currentMonth)
        //  TODO 해당 함수 호출 시, FinanceFragment의 연, 월 변경 필요
    }
    private fun dateClicked(date: LocalDate) {
        binding.calendar.notifyDateChanged(selectedDate) // 이전 선택값 해제

        selectedDate = date
        binding.calendar.notifyDateChanged(date) // 새로운 선택값

//        val element = viewModel.scheduleList.value!!.find { LocalDate.parse(it.eventDay, format) == date }
//        if (element != null) {
//            viewModel.setSchedules(element)
//            viewModel.setSelectedDate(element.eventDay)
            val dialog = PaymentCalendarBottomSheetFragment()
            dialog.show(childFragmentManager, "payment")
//        } else {
//            viewModel.setSelectedDate(null)
//        }
    }

    private fun bindDate(date: LocalDate, dayText: TextView, paymentText: TextView, isSelectable: Boolean) {
        dayText.text = date.dayOfMonth.toString()
        if(date.dayOfMonth % 8 == 0) paymentText.text = resources.getString(R.string.finance_calendar_payment, CommonUtils.makeComma(18000))
        val fonts = arrayOf(R.font.pretendard_regular, R.font.pretendard_bold)

        if (isSelectable) {
//            val element = viewModel.scheduleList.value!!.find { LocalDate.parse(it.eventDay, format) == date }
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
        } else {
            dayText.setTextColor(resources.getColor(R.color.disabled))
        }
    }

}