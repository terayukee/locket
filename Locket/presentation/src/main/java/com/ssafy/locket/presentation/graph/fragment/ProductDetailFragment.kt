package com.ssafy.locket.presentation.graph.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.model.graph.product_detail.PriceHistoryInfo
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentProductDetailBinding
import com.ssafy.locket.presentation.graph.PriceMarkerView
import com.ssafy.locket.presentation.graph.viewmodel.EditPriceViewModel
import com.ssafy.locket.presentation.graph.viewmodel.ProductDetailState
import com.ssafy.locket.presentation.graph.viewmodel.ProductHappyListState
import com.ssafy.locket.presentation.graph.viewmodel.ProductViewModel
import com.ssafy.locket.presentation.login.LoginViewModel
import com.ssafy.locket.presentation.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private const val TAG = "CategoryProductListFrag"
@AndroidEntryPoint
class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(
    FragmentProductDetailBinding::bind,
    R.layout.fragment_product_detail
) {
    private val viewModel: EditPriceViewModel by activityViewModels()
    val bottomSheet = EditPriceBottomSheetFragment.newInstance()
    //카테고리 번호 알기 위함
    var productId = -1
    private val productViewModel: ProductViewModel by activityViewModels()
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource
    //차트
    private lateinit var lineChart: LineChart
    //하트 색칠여부
    private var isHeartFilled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initEvent()
        initViewModel()
        getDetailInfo()
        initChart()
    }

    override fun onPause() {
        super.onPause()
        productViewModel.productLikeClick(productId,isHeartFilled)
    }

    fun initData(){
        productId = arguments?.getInt("productId") ?: -1
        Log.d(TAG,"무슨 값"+productId.toString())
        lifecycleScope.launch {
            val user = userDataStoreSource.user.first()
            user?.let {it->
                productViewModel.getDetailInfo(productId,it.userId)
            }
        }
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
                binding.cvNotificationSetting.visibility = View.INVISIBLE
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
    }

    private fun setupLineChart(priceHistory: List<PriceHistoryInfo>) {
        val highPriceEntries = ArrayList<Entry>()
        val lowPriceEntries = ArrayList<Entry>()
        val dates = ArrayList<String>()
        val leftAxis: YAxis = lineChart.axisLeft

        leftAxis.removeAllLimitLines()

        for (i in priceHistory.indices) {
            val history = priceHistory[i]
            dates.add(history.priceDate)
            highPriceEntries.add(Entry(i.toFloat(), history.highestPrice.toFloat()))
            lowPriceEntries.add(Entry(i.toFloat(), history.lowestPrice.toFloat()))
        }

        val adjustedHighPriceEntries = ArrayList<Entry>()
        val adjustedLowPriceEntries = ArrayList<Entry>()

        for (i in priceHistory.indices) {
            val high = highPriceEntries[i].y
            val low = lowPriceEntries[i].y

            if (high == low) {
                // 최고가와 최저가가 같으면 그대로 사용
                adjustedHighPriceEntries.add(Entry(i.toFloat(), high))
                adjustedLowPriceEntries.add(Entry(i.toFloat(), low))
            } else {
                val diff = high - low
                val adjustment = diff * 0.05f // 차이의 5%만큼 조정

                adjustedHighPriceEntries.add(Entry(i.toFloat(), high + adjustment))
                adjustedLowPriceEntries.add(Entry(i.toFloat(), low - adjustment))
            }
        }

        val highPriceDataSet = LineDataSet(adjustedHighPriceEntries, "최고가").apply {
            color = Color.RED
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
        }

        val lowPriceDataSet = LineDataSet(adjustedLowPriceEntries, "최저가").apply {
            color = Color.parseColor("#C9C9C9")
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
        }

        val maxPrice = highPriceEntries.maxOfOrNull { it.y } ?: 0f
        val minPrice = lowPriceEntries.minOfOrNull { it.y } ?: 0f
        val avgPrice = (maxPrice + minPrice) / 2

        leftAxis.apply {
            addLimitLine(LimitLine(avgPrice, "6개월 평균가: ${String.format("%.1f", avgPrice)}원").apply {
                lineColor = Color.parseColor("#00CBBF")
                lineWidth = 2f
                labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
                enableDashedLine(10f, 10f, 0f)
            })

            addLimitLine(LimitLine(minPrice, "6개월 최저가: ${minPrice}원").apply {
                lineColor = Color.RED
                textColor = Color.RED
                lineWidth = 2f
                labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                enableDashedLine(10f, 10f, 0f)
            })

            axisMinimum = minPrice * 0.9f
            axisMaximum = maxPrice * 1.1f
            setDrawGridLines(false)
        }

        lineChart.apply {
            data = LineData(highPriceDataSet, lowPriceDataSet)
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(dates)
                setDrawGridLines(false)
            }
            setPinchZoom(false)
            setDragEnabled(true)
            setScaleXEnabled(true)
            setScaleYEnabled(false)
            setDoubleTapToZoomEnabled(false)
            marker = PriceMarkerView(requireContext()).apply {
                setData(dates.toTypedArray(), highPriceEntries.map { it.y.toInt() }.toIntArray(), lowPriceEntries.map { it.y.toInt() }.toIntArray())
            }
            invalidate()
        }

        lineChart.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 부모 스크롤뷰의 스크롤 막기
                    v.parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP -> {
                    // 터치가 끝나면 부모 스크롤뷰의 스크롤 허용
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
                MotionEvent.ACTION_CANCEL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            // 차트의 기본 터치 이벤트 처리
            false
        }
    }

    fun getDetailInfo(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productDetailInfo.collect { productDetail ->
                    if (productDetail is ProductDetailState.Success) {
                        launch { binding.tvProductTitle.text = productDetail.productDetailInfo.productName }
                        launch { binding.tvPrice.text = CommonUtils.makeComma(productDetail.productDetailInfo.currentPrice.toInt()) + "원" }
                        launch {
                            Glide.with(requireContext())
                                .load(productDetail.productDetailInfo.imageUrl)
                                .thumbnail(0.1f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(binding.ivProductImage)
                        }
                        launch { binding.tvReview.text = productDetail.productDetailInfo.reviewRating }
                        launch {
                            setupLineChart(productDetail.productDetailInfo.priceHistory)
                        }
                    }
                }
            }
        }
    }

}