package com.ssafy.locket.presentation.finance.fragment.analysis

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        financeSharedViewModel.initYearMonth()
//        binding.progressBar.visibility = View.VISIBLE

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
                        Log.d(TAG,getFeedback.feedback.toString())
                        val feedback = getFeedback.feedback
                        setupPieChart()
                        binding.progressBar.visibility = View.GONE
                        binding.groupAnalysis.visibility = View.VISIBLE
                        binding.tvNoAnalysis.visibility = View.GONE
                        loadPieChartData(feedback.categoryBreakdownList)
                        binding.tvFeedbackContent.text = "${feedback.summary}\n\n${feedback.insights}\n\n${feedback.recommendations}"
                        categoryPaymentRVAdapter.submitList(getFeedback.feedback.categoryBreakdownList.items)
                    } else if(getFeedback is GetFeedbackState.Error) {
                        Log.d(TAG, "getFeedbackData: Error")
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
        }
    }

    private fun loadPieChartData(categoryBreakdownList: CategoryBreakdownList) {
        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        // 각 카테고리별로 사용자 정의 색상 설정
        val categoryColors = mapOf(
            "식비" to Color.parseColor("#FFB602"),
            "쇼핑" to Color.parseColor("#EB4634"),
            "생활" to Color.parseColor("#C59A6E"),
            "교통" to Color.parseColor("#8D73A8"),
            "카페/디저트" to Color.parseColor("#8E9FE2"),
            "기타" to Color.parseColor("#9E9E9E")
        )

        for (i in 0 until categoryBreakdownList.items.size) {
            val item = categoryBreakdownList.items.get(i)

            entries.add(PieEntry(item.percentage.toFloat(), item.category))

            categoryColors[item.category]?.let { colors.add(it) }
        }

        var characterName: String = "기타리스트"
        var characterImg: Int = R.drawable.image_analysis_guitarist
        when(categoryBreakdownList.items.get(0).category) {
            "쇼핑" -> {
                characterName = "풀소유 플렉스 챔피언"
                characterImg = R.drawable.image_analysis_full_flex_champion
            }
            "카페/디저트" -> {
                characterName = "카페인 뱀파이어형"
                characterImg = R.drawable.image_analysis_caffeine_vampire
            }
            "식비" -> {
                characterName = "맛의 방랑자"
                characterImg = R.drawable.image_analysis_taste_nomad
            }
            "생활" -> {
                characterName = "생활의 달인"
                characterImg = R.drawable.image_analysis_home
            }
            "교통" -> {
                characterName = "도로위의 방랑자"
                characterImg = R.drawable.image_analysis_transportation
            }
        }

        Glide.with(requireContext())
            .load(characterImg)
            .into(binding.ivCategoryCharacter)

        binding.tvCategoryType.text = characterName

        val dataSet = PieDataSet(entries, "지출 카테고리")
        dataSet.colors = colors
        dataSet.sliceSpace = 3f
        dataSet.setDrawValues(false)

        // PieData 생성 및 설정
        val data = PieData(dataSet)

        // 차트에 데이터 설정
        binding.pieChart.data = data
        binding.pieChart.invalidate() // 차트 갱신

        // 애니메이션 효과 추가
        binding.pieChart.animateY(1000)
    }
}