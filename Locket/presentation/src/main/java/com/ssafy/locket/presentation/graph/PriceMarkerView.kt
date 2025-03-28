package com.ssafy.locket.presentation.graph

import android.content.Context
import android.widget.TextView
import com.example.locket.CommonUtils
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.ssafy.locket.presentation.R

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
            tvLow.text = "최저가 ${CommonUtils.makeComma(lowPrices[index]-300)}원"
            tvDate.text = dates[index]
        }
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}