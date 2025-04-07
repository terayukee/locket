package com.ssafy.locket.presentation.finance.fragment.budget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.provider.CalendarContract.Colors
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.ssafy.locket.presentation.R

private const val TAG = "CustomProgressBar"
class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val horizontalPadding: Float = 4f
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val progressBar: ProgressBar
    private val tvPercent: View

    init {
        progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
            id = View.generateViewId()
            max = 100
            progress = 0
            progressDrawable = ContextCompat.getDrawable(context, R.drawable.bg_finance_budget_progress)
        }

        tvPercent = PercentageTextView(context).apply {
            id = View.generateViewId()
        }

        addView(progressBar, LayoutParams(
            LayoutParams.MATCH_CONSTRAINT, // 0dp
            LayoutParams.MATCH_CONSTRAINT
        ))
        addView(tvPercent, LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_CONSTRAINT
        ))

        // ConstraintSet으로 레이아웃 설정
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        // 프로그레스바 레이아웃 설정
        constraintSet.connect(progressBar.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(progressBar.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(progressBar.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(progressBar.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

        // 퍼센트 텍스트뷰 레이아웃 설정
        constraintSet.connect(tvPercent.id, ConstraintSet.TOP, progressBar.id, ConstraintSet.TOP)
        constraintSet.connect(tvPercent.id, ConstraintSet.BOTTOM, progressBar.id, ConstraintSet.BOTTOM)

        constraintSet.applyTo(this)
    }

    fun setProgress(progress: Int) {
        val limitedProgress = progress.coerceIn(0, 100)
        progressBar.progress = limitedProgress
        (tvPercent as PercentageTextView).setProgress(progress)
        if(progress > 100) progressBar.progressDrawable = ContextCompat.getDrawable(context, R.drawable.bg_finance_budget_progress_over)
        else{
            progressBar.progressDrawable = ContextCompat.getDrawable(context, R.drawable.bg_finance_budget_progress)
        }
    }

    inner class PercentageTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 12f * resources.displayMetrics.scaledDensity // px -> sp로 변환
            textAlign = Paint.Align.CENTER
            typeface = ResourcesCompat.getFont(context, R.font.pretendard_bold) // 폰트 적용
        }
        private var progress = 0
        private var percentText = "$progress%"

        fun setProgress(newProgress: Int) {
            progress = newProgress
            percentText = "$progress%"
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // 텍스트 너비 계산
            val textWidth = paint.measureText(percentText)

            // 진행 영역 계산
            val progressWidth = width.toFloat()

            val paddingPx = (horizontalPadding * resources.displayMetrics.density)

            var textX : Float

            // 텍스트 x 좌표 계산
            textX = if(progress > 100) (progressWidth * (100 / 100f)) - textWidth / 2 - paddingPx
            else if(progress == 0) textWidth + paddingPx
            else (progressWidth * (progress / 100f)) - textWidth / 2 - paddingPx

            if (progress == 0) paint.color = resources.getColor(R.color.disabled)
            else paint.color = Color.WHITE
             // 텍스트 그리기
            canvas.drawText(percentText, textX, height / 2f + paint.textSize / 2f, paint)
        }

        /*
             // 텍스트 너비 계산
    val textWidth = paint.measureText(percentText)
    val progressWidth = width.toFloat()
    val paddingPx = horizontalPadding * resources.displayMetrics.density

    // 진행 영역 계산 (최대 100%로 제한)
    val effectiveProgress = if (progress > 100) 100f else progress.toFloat()
    val progressPosition = progressWidth * (effectiveProgress / 100f)

    // 텍스트 x 좌표: 진행 위치 + 패딩 (오른쪽 정렬)
    var textX = progressPosition + paddingPx

    // 텍스트가 화면을 벗어나지 않도록 보정
    if (textX + textWidth > progressWidth) {
        textX = progressWidth - textWidth
    }
         */
    }

}