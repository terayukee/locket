package com.ssafy.locket.presentation.graph.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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
import com.ssafy.locket.presentation.graph.viewmodel.ProductViewModel
import com.ssafy.locket.presentation.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CategoryProductListFrag"
@AndroidEntryPoint
class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(
    FragmentProductDetailBinding::bind,
    R.layout.fragment_product_detail
) {
    private val editViewModel: EditPriceViewModel by activityViewModels()
    val bottomSheet = EditPriceDialogFragment.newInstance()
    //카테고리 번호 알기 위함
    var productId = -1
    private val productViewModel: ProductViewModel by activityViewModels()
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource
    //차트
    private lateinit var lineChart: LineChart
    //하트 색칠여부
    private var isHeartFilled = false

    var url = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initViewModel()
        getDetailInfo()
        initChart()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onStop() {
        super.onStop()
        if(editViewModel.editprice.value==""){
            productViewModel.productAlert(productId,false,0)
        }
        else{
            val price = editViewModel.editprice.value.replace(",", "").toInt()
            productViewModel.productAlert(productId, true, price)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        editViewModel.updatePrice("")
        productViewModel.resetProductDetailState()
    }
    fun initData(){
        productId = arguments?.getInt("productId") ?: -1
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
            if (parentFragmentManager.findFragmentByTag(EditPriceDialogFragment.TAG) == null) {
                bottomSheet.show(parentFragmentManager, EditPriceDialogFragment.TAG)
            }
        }
        binding.ivLikeBtn.expandTouchArea(100)
        binding.ivLikeBtn.setOnClickListener {
            isHeartFilled = !isHeartFilled
            if (isHeartFilled) {
                binding.cvNotificationSetting.visibility = View.VISIBLE
                binding.ivCoupangMove.visibility = View.VISIBLE
                binding.ivLikeBtn.setImageResource(R.drawable.ic_graph_heart)  // 색칠된 하트
            } else {
                binding.cvNotificationSetting.visibility = View.INVISIBLE
                binding.ivCoupangMove.visibility = View.INVISIBLE
                binding.ivLikeBtn.setImageResource(R.drawable.ic_all_empty_heart)  // 빈 하트
            }
            productViewModel.productLikeClick(productId,isHeartFilled)
        }
        binding.ivCoupangMove.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    fun initViewModel(){
        lifecycleScope.launchWhenStarted {
            editViewModel.editprice.collect { editprice ->
                if(editprice==""){
                    binding.tvSetting.text = "설정 안됨"
                    binding.cvNotificationSetting.setCardBackgroundColor(Color.parseColor("#DADADA"))
                }
                else{
                    binding.tvSetting.text = CommonUtils.formatNumber(editprice)+"원"
                    binding.cvNotificationSetting.setCardBackgroundColor(Color.parseColor("#00CBBF"))
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

        Log.d(TAG, "setupLineChart: highPriceEntries ${highPriceEntries.map { println("${it.x}  ${it.y} ${it.data}")}}")
        Log.d(TAG, "setupLineChart: lowPriceEntries ${lowPriceEntries.map { println("${it.x}  ${it.y} ${it.data}")}}")
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
            color = Color.parseColor("#C9C9C9")
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
        }

        val lowPriceDataSet = LineDataSet(adjustedLowPriceEntries, "최저가").apply {
            color = Color.RED
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
        }

        val maxPrice = highPriceEntries.maxOfOrNull { it.y } ?: 0f
        val minPrice = lowPriceEntries.minOfOrNull { it.y } ?: 0f

        val avgPrice = (maxPrice + minPrice) / 2

        leftAxis.apply {
            addLimitLine(LimitLine(avgPrice, "6개월 평균가: ${avgPrice.toInt()}원").apply {
                lineColor = Color.parseColor("#00CBBF")
                lineWidth = 2f
                labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
                enableDashedLine(10f, 10f, 0f)
            })

            addLimitLine(LimitLine(minPrice, "6개월 최저가: ${minPrice.toInt()}원").apply {
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
            data = LineData(highPriceDataSet,lowPriceDataSet)
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
                productViewModel.productDetailInfo.collectLatest { productDetail ->
                    if(productDetail is ProductDetailState.Success) {
                        binding.tvProductTitle.text = productDetail.productDetailInfo.productName
                        binding.tvPrice.text = productDetail.productDetailInfo.currentPrice
                        Glide.with(requireContext())
                            .load(productDetail.productDetailInfo.imageUrl)
                            .placeholder(ColorDrawable(Color.WHITE))
                            .error(ColorDrawable(Color.WHITE))
                            .into(binding.ivProductImage)
                        binding.tvReview.text = productDetail.productDetailInfo.reviewRating
                        binding.tvHighestPrice.text= productDetail.productDetailInfo.highestPrice
                        binding.tvDiscount.text = productDetail.productDetailInfo.discountRate
                        url = productDetail.productDetailInfo.coupangUrl
                        setupLineChart(productDetail.productDetailInfo.priceHistory)
                        isHeartFilled = productDetail.productDetailInfo.liked
                        binding.ivLikeBtn.visibility = View.VISIBLE
                        binding.ivStar.visibility = View.VISIBLE
                        binding.chartPriceGraph.visibility = View.VISIBLE
                        binding.vUnderline.visibility = View.VISIBLE
                        binding.tvChartTitle.visibility = View.VISIBLE
                        binding.tvChartDescription.visibility = View.VISIBLE
                        if(isHeartFilled){
                            binding.ivLikeBtn.setImageResource(R.drawable.ic_graph_heart)
                            binding.cvNotificationSetting.visibility = View.VISIBLE
                            binding.ivCoupangMove.visibility = View.VISIBLE
                            if(productDetail.productDetailInfo.alertPrice==0){
                                binding.tvSetting.text ="설정 안됨"
                                binding.cvNotificationSetting.setCardBackgroundColor(Color.parseColor("#DADADA"))
                                editViewModel.updatePrice("")
                            }
                            else{
                                binding.tvSetting.text = CommonUtils.makeComma(productDetail.productDetailInfo.alertPrice)+"원"
                                binding.cvNotificationSetting.setCardBackgroundColor(Color.parseColor("#00CBBF"))
                                editViewModel.updatePrice(CommonUtils.makeComma(productDetail.productDetailInfo.alertPrice))
                            }
                        }
                        else{
                            binding.ivLikeBtn.setImageResource(R.drawable.ic_all_empty_heart)
                            binding.ivCoupangMove.visibility = View.INVISIBLE
                            binding.cvNotificationSetting.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }

    fun View.expandTouchArea(extraPadding: Int) {
        val parent = this.parent as View
        parent.post {
            val rect = Rect()
            this.getHitRect(rect)
            rect.top -= extraPadding
            rect.bottom += extraPadding
            rect.left -= extraPadding
            rect.right += extraPadding
            parent.touchDelegate = TouchDelegate(rect, this)
        }
    }
}