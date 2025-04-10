package com.ssafy.locket.presentation.graph

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.utils.CommonUtils

class PriceMarkerView(context: Context) : MarkerView(context, R.layout.custom_marker_view) {
    private val tvLow: TextView = findViewById(R.id.tv_low)
    private val tvDate: TextView = findViewById(R.id.tv_date)
    private val tvHigh: TextView = findViewById(R.id.tv_high)

    private lateinit var dates: Array<String>
    private lateinit var highPrices: IntArray
    private lateinit var lowPrices: IntArray

    fun setData(dates: Array<String>, highPrices: IntArray, lowPrices: IntArray) {
        this.dates = dates
        this.highPrices = highPrices
        this.lowPrices = lowPrices
    }

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        if (entry != null && highlight != null) {
            val index = entry.x.toInt()
            tvHigh.text = "최고가 ${CommonUtils.makeComma(highPrices[index])}원"
            tvLow.text = "최저가 ${CommonUtils.makeComma(lowPrices[index])}원"
            tvDate.text = dates[index]
        }
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF {
        val chartLocation = IntArray(2)
        val markerLocation = IntArray(2)

        // 차트의 위치와 마커의 위치를 가져옵니다
        (parent as? ViewGroup)?.getLocationInWindow(chartLocation)
        getLocationInWindow(markerLocation)

        // 차트의 실제 너비와 높이를 계산합니다
        val chartWidth = (parent as? ViewGroup)?.width ?: 0

        // 마커의 오프셋 계산
        val xOffset = -(width / 2f)  // 마커가 차트에서 중앙에 오도록 설정
        val yOffset = -height.toFloat()

        // 화면의 크기 가져오기
        val displayMetrics = resources.displayMetrics

        // 오른쪽 가장자리가 차트를 벗어나지 않도록 조정
        val rightEdgeOffset = markerLocation[0] + width + xOffset
        if (rightEdgeOffset > chartWidth) {
            return MPPointF(chartWidth - markerLocation[0] - width.toFloat(), yOffset) // 차트의 오른쪽을 벗어나지 않도록 조정
        }

        // 마커가 차트의 범위 안에 있을 경우
        return MPPointF(xOffset, yOffset)
    }
}