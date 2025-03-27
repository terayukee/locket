package com.ssafy.locket.ui.graph

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.locket.CommonUtils
import com.github.mikephil.charting.charts.LineChart
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentProductDetailBinding
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.ssafy.locket.ui.graph.viewmodel.EditPriceViewModel

class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(
    FragmentProductDetailBinding::bind,
    R.layout.fragment_product_detail
) {
    private val viewModel: EditPriceViewModel by activityViewModels()
    val bottomSheet = EditPriceBottomSheetFragment.newInstance()
    //차트
    private lateinit var lineChart: LineChart
    //하트 색칠여부
    private var isHeartFilled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initViewModel()
        initChart()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.cvNotificationSetting.setOnClickListener {
            bottomSheet.show(parentFragmentManager, EditPriceBottomSheetFragment.TAG)
        }
        binding.ivLikeBtn.setOnClickListener {
            isHeartFilled = !isHeartFilled
            if (isHeartFilled) {
                binding.cvNotificationSetting.visibility = View.VISIBLE
                binding.ivLikeBtn.setImageResource(R.drawable.ic_graph_heart)  // 색칠된 하트
            } else {
                binding.cvNotificationSetting.visibility = View.GONE
                binding.ivLikeBtn.setImageResource(R.drawable.ic_all_empty_heart)  // 빈 하트
            }
        }
    }

    fun initViewModel(){
        lifecycleScope.launchWhenStarted {
            viewModel.editprice.collect { editprice ->
                if(editprice==""){
                    binding.tvSetting.text = "설정 안됨"
                }
                else{
                    binding.tvSetting.text = CommonUtils.formatNumber(editprice)+"원"
                }
            }
        }
    }

    fun initChart(){
        lineChart = binding.chartPriceGraph
        setupLineChart()
    }

    private fun setupLineChart() {
        val highPriceEntries = ArrayList<Entry>()
        val lowPriceEntries = ArrayList<Entry>()
        // 날짜별 데이터 (X축: 날짜, Y축: 가격)
        val dates = arrayOf(
            "09-11", "09-12", "09-14", "09-15", "09-17", "09-18", "09-20", "09-21",
            "09-23", "09-24", "09-26", "09-27", "09-29", "09-30", "10-02", "10-03",
            "10-05", "10-06", "10-08", "10-09", "10-11", "10-12", "10-14", "10-15",
            "10-17", "10-19", "10-20", "10-22", "10-23", "10-25", "10-26", "10-28",
            "10-29", "10-31", "11-01", "11-03", "11-04", "11-06", "11-07", "11-09",
            "11-10", "11-12", "11-13", "11-15", "11-16", "11-18", "11-19", "11-21",
            "11-23", "11-24", "11-26", "11-27", "11-29", "11-30", "12-02", "12-03",
            "12-05", "12-06", "12-08", "12-09", "12-11", "12-12", "12-14", "12-15",
            "12-17", "12-18", "12-20", "12-21", "12-23", "12-24", "12-26", "12-27",
            "12-29", "12-31", "01-01", "01-03", "01-04", "01-06", "01-07", "01-09",
            "01-10", "01-12", "01-13", "01-15", "01-16", "01-18", "01-19", "01-21",
            "01-22", "01-24", "01-25", "01-27", "01-28", "01-30", "01-31", "02-02",
            "02-04", "02-05", "02-07", "02-08", "02-10", "02-11", "02-13", "02-14",
            "02-16", "02-17", "02-19", "02-20", "02-22", "02-23", "02-25", "02-26",
            "02-28", "03-01", "03-03", "03-04", "03-06", "03-07", "03-09", "03-11"
        )
        val highPrices = intArrayOf(
            18480, 18480, 18480, 18480, 18480, 18480, 18480, 18480,
            18480, 18480, 18480, 18480, 18480, 18210, 18210, 18210,
            18210, 18210, 18480, 18480, 18480, 18480, 18480, 18450,
            18450, 18480, 18480, 18480, 18480, 18210, 17370, 17370,
            17370, 17670, 17670, 17670, 17670, 18400, 18180, 17960,
            17740, 17740, 17740, 18480, 18360, 18360, 18360, 18360,
            18360, 18360, 18360, 18360, 18360, 18360, 18360, 18360,
            18360, 18270, 18070, 17870, 17870, 17860, 17860, 17860,
            17860, 17860, 18360, 18360, 18360, 18360, 18360, 18360,
            18360, 18360, 18360, 18360, 18360, 18360, 17950, 17950,
            18360, 18360, 18360, 18360, 18360, 18360, 18260, 18260,
            18260, 18260, 18260, 18360, 18360, 18360, 18360, 18360,
            18360, 18360, 18360, 18360, 18360, 18360, 17950, 17950,
            18360, 18360, 18360, 18360, 18360, 18360, 18260, 18260,
            18260, 18260, 18260, 18360, 18360, 18360, 18360, 18360
        )
        val lowPrices = highPrices // 최저가도 같은 배열 사용
        for (i in dates.indices) {
            //lowPriceEntries.add(Entry(i.toFloat(), lowPrices[i].toFloat()))
            highPriceEntries.add(Entry(i.toFloat(), highPrices[i].toFloat()))
            lowPriceEntries.add(Entry(i.toFloat(), lowPrices[i].toFloat()+300))
        }
        // 최고가 라인
        val highPriceDataSet = LineDataSet(highPriceEntries, "최고가").apply {
            color = Color.BLUE
            lineWidth = 2f
            setDrawCircles(false) // 원형 점 숨기기
            setDrawCircleHole(false) // 원 내부 구멍 숨기기
            valueTextSize = 10f
        }
        // 최저가 라인
        val lowPriceDataSet = LineDataSet(lowPriceEntries, "최저가").apply {
            color = Color.RED
            lineWidth = 2f
            setDrawCircles(false) // 원형 점 숨기기
            setDrawCircleHole(false) // 원 내부 구멍 숨기기
            valueTextSize = 10f
        }
        // 평균가 표시 (17,925원)
        val leftAxis: YAxis = lineChart.axisLeft
        val limitLine = LimitLine(17925f, "6개월간 평균가").apply {
            lineColor = Color.parseColor("#00CBBF")
            lineWidth = 2f
            textColor = Color.parseColor("#00CBBF")
            labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
            enableDashedLine(10f, 10f, 0f) // 점선 스타일 (10px 선, 10px 공백)
        }
        leftAxis.addLimitLine(limitLine)
        val minPrice = lowPrices.minOrNull()?.toFloat() ?: 0f // 최저가 값 찾기
        val lowPriceLimitLine = LimitLine(minPrice, "6개월간 최저가"+minPrice+"원").apply {
            lineColor = Color.parseColor("#FF0000") // 빨간색
            lineWidth = 2f
            textColor = Color.parseColor("#FF0000") // 텍스트 색상
            labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM // 왼쪽 상단에 표시
            enableDashedLine(10f, 10f, 0f) // 점선 스타일 (10px 선, 10px 공백)
        }
        // Y축에 추가
        leftAxis.addLimitLine(lowPriceLimitLine)
        // 데이터 적용
        lineChart.data = LineData(highPriceDataSet, lowPriceDataSet)
        lineChart.invalidate()
        // X축 설정
        val xAxis: XAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(dates)
        // 설명 제거
        lineChart.description = Description().apply { text = "" }
        // 범례 설정
        val legend: Legend = lineChart.legend
        legend.isEnabled = true
    }
}