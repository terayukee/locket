package com.ssafy.locket.presentation.finance.fragment.analysis

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.ssafy.locket.model.finance.budget.feedback.CategoryBreakdownList
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentExpenseAnalysisBinding
import com.ssafy.locket.presentation.finance.adapter.CategoryPaymentRVAdapter
import com.ssafy.locket.presentation.finance.viewmodel.AnalysisViewModel
import com.ssafy.locket.presentation.finance.viewmodel.FinanceSharedViewModel
import com.ssafy.locket.presentation.finance.viewmodel.GetFeedbackState
import com.ssafy.locket.presentation.finance.viewmodel.TotalPaymentState
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.YearMonth
import com.lottiefiles.dotlottie.core.model.Config
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ExpenseAnalysisFragment"
@AndroidEntryPoint
class ExpenseAnalysisFragment : BaseFragment<FragmentExpenseAnalysisBinding>(
    FragmentExpenseAnalysisBinding::bind,
    R.layout.fragment_expense_analysis
) {
    private lateinit var categoryPaymentRVAdapter: CategoryPaymentRVAdapter

    private val analysisViewModel : AnalysisViewModel by viewModels()
    private val financeSharedViewModel: FinanceSharedViewModel by activityViewModels()

    private val startMonth = YearMonth.of(2020, 1)
    private val endMonth = YearMonth.now()

    private var tooltipWindow: PopupWindow? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        financeSharedViewModel.initYearMonthPayment()

        binding.progressBar.load(
            Config.Builder()
                .source(DotLottieSource.Asset("analysis_loading.lottie"))
                .autoplay(true)
                .loop(true)
                .speed(0.7f)
                .build()
        )
        initAdapter()
        initEvent()
        getFeedbackData()
    }

    override fun onDestroyView() {
        tooltipWindow?.dismiss() // 툴팁 제거
        tooltipWindow = null
        super.onDestroyView()
    }

    fun initEvent(){
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                financeSharedViewModel.selectedYearMonth.collectLatest {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvNoAnalysis.visibility = View.GONE
                    binding.groupAnalysis.visibility = View.GONE
                    analysisViewModel.getFeedback(it.year,it.monthValue)
                    binding.tvYearMonth.text = resources.getString(R.string.finance_year_month, it.year, it.monthValue)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            financeSharedViewModel.selectedYearMonthTotalPayment.collectLatest { uiState ->
                when(uiState) {
                    is TotalPaymentState.Success -> {
                        binding.tvPaymentData.text = resources.getString(R.string.finance_won, CommonUtils.makeCommaDecimal(uiState.totalPayment))

                    }
                    is TotalPaymentState.Error -> {
                        Log.d(TAG, "initUI: Error payment ${uiState.message}")
                        CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.ERROR, uiState.message)
                    }
                    else -> Log.d(TAG, "initUI: Payment Initial or Loading")
                }
            }
        }
    }

    private fun updateTitle(month: YearMonth) {
        binding.tvYearMonth.text = getString(R.string.finance_year_month, month.year, month.monthValue)
        financeSharedViewModel.setYearMonth(month)
    }

    private fun initAdapter() {
        categoryPaymentRVAdapter = CategoryPaymentRVAdapter()

        binding.rvCategoryPayment.apply {
            adapter = categoryPaymentRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun getFeedbackData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                analysisViewModel.getFeedback.collectLatest{ getFeedback ->
                    if(getFeedback is GetFeedbackState.Success) {
                        val feedback = getFeedback.feedback
                        setupPieChart()
                        binding.progressBar.visibility = View.GONE
                        binding.groupAnalysis.visibility = View.VISIBLE
                        binding.tvNoAnalysis.visibility = View.GONE
                        loadPieChartData(feedback.categoryBreakdownList)
                        binding.tvFeedbackContent.text = "${feedback.summary}\n\n${feedback.insights}\n\n${feedback.recommendations}"
                        categoryPaymentRVAdapter.submitList(feedback.categoryBreakdownList.items)
                    } else if(getFeedback is GetFeedbackState.Error) {
                        binding.progressBar.visibility = View.GONE
                        binding.tvNoAnalysis.visibility = View.VISIBLE
                        binding.groupAnalysis.visibility = View.GONE
                    } else {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvNoAnalysis.visibility = View.GONE
                        binding.groupAnalysis.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            isDrawHoleEnabled = true
            setUsePercentValues(true)
            isRotationEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            setDrawEntryLabels(false) // ✅ 항목 라벨 제거
        }
    }

    private fun loadPieChartData(categoryBreakdownList: CategoryBreakdownList) {
        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        val categoryColors = mapOf(
            "식비" to Color.parseColor("#8E9FE2"),
            "쇼핑" to Color.parseColor("#EB4634"),
            "생활" to Color.parseColor("#C59A6E"),
            "교통" to Color.parseColor("#8D73A8"),
            "카페/디저트" to Color.parseColor("#FFB602"),
            "기타" to Color.parseColor("#9E9E9E")
        )

        categoryBreakdownList.items.forEach { item ->
            entries.add(PieEntry(item.percentage.toFloat(), item.category))
            categoryColors[item.category]?.let { colors.add(it) }
        }

        val (characterName, characterImg) = when(categoryBreakdownList.items.firstOrNull()?.category) {
            "쇼핑" -> "풀소유 플렉스 챔피언" to R.drawable.image_analysis_full_flex_champion
            "카페/디저트" -> "카페인 뱀파이어형" to R.drawable.image_analysis_caffeine_vampire
            "식비" -> "맛의 방랑자" to R.drawable.image_analysis_taste_nomad
            "생활" -> "생활의 달인" to R.drawable.image_analysis_home
            "교통" -> "도로위의 방랑자" to R.drawable.image_analysis_transportation
            else -> "기타리스트" to R.drawable.image_analysis_guitarist
        }

        Glide.with(requireContext()).load(characterImg).into(binding.ivCategoryCharacter)
        binding.tvCategoryType.text = characterName

        val dataSet = PieDataSet(entries, "지출 카테고리").apply {
            this.colors = colors
            sliceSpace = 3f
            setDrawValues(false)
        }

        binding.pieChart.apply {
            data = PieData(dataSet)
            invalidate()
            animateY(1000)
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    tooltipWindow?.dismiss()
                    if (e is PieEntry) showTooltip(e.label,h!!)
                }
                override fun onNothingSelected() {
                    tooltipWindow?.dismiss()
                }
            })
        }
    }


    private fun showTooltip(category: String, highlight: Highlight) {
        val inflater = layoutInflater
        val tooltipView = inflater.inflate(R.layout.tooltip_chart, null)
        val tvCategory = tooltipView.findViewById<TextView>(R.id.tv_category)
        tvCategory.text = category
        tooltipWindow?.dismiss() // 기존 툴팁 제거

        tooltipWindow = PopupWindow(
            tooltipView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            elevation = 10f
        }
        // 차트의 화면 내 위치
        val chartLocation = IntArray(2)
        binding.pieChart.getLocationOnScreen(chartLocation)
        // 선택된 조각의 좌표 (화면 기준)
        val touchX = chartLocation[0] + highlight.xPx.toInt()
        val touchY = chartLocation[1] + highlight.yPx.toInt()

        // 툴팁 표시: 선택한 조각 위쪽에 약간 띄워서 표시
        tooltipWindow?.showAtLocation(binding.pieChart, Gravity.NO_GRAVITY, touchX, touchY - 150)
    }
}
