package com.ssafy.locket.presentation.utils

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.ssafy.locket.presentation.databinding.ToastMultiLineCustomBinding
import com.ssafy.locket.presentation.databinding.ToastSingleLineCustomBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CommonUtils {
    //천단위 콤마
    fun makeComma(num: Int): String {
        val comma = DecimalFormat("#,###")
        return comma.format(num)
    }

    //날짜 포맷 출력
    fun dateformatYMDHM(time:Date):String{
        val format = SimpleDateFormat("yyyy.MM.dd. HH:mm", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(time)
    }

    fun dateformatYMD(time: Date):String{
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(time)
    }

    fun formatLongToDate(longDate: Long): String {
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())  // 원하는 날짜 형식 지정
        return format.format(Date(longDate))  // Long 값을 Date 객체로 변환 후 포맷 적용
    }

    fun formatNumber(number: String): String {
        return try {
            val num = number.toLong()
            NumberFormat.getNumberInstance(Locale.KOREA).format(num)
        } catch (e: Exception) {
            number
        }
    }

    fun showMultiLineCustomToast(context: Context, title: String, content: String) {
        val inflater = LayoutInflater.from(context)
        val binding = ToastMultiLineCustomBinding.inflate(inflater)

        binding.tvTitle.text = title
        binding.tvContent.text = content

//        if(title.contains("권한")) binding.layout.backgroundTintList = context.getColorStateList(R.color.white)
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = binding.root
        toast.setGravity(Gravity.BOTTOM, 0, 200)
        toast.show()
    }

    fun showSingleLineCustomToast(context: Context, type: ToastType, content: String) {
        val inflater = LayoutInflater.from(context)
        val binding = ToastSingleLineCustomBinding.inflate(inflater)

        binding.tvContent.text = content
        if(type == ToastType.ERROR) binding.icError.visibility = View.VISIBLE
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = binding.root
        toast.setGravity(Gravity.BOTTOM, 0, 200)
        toast.show()
    }

    fun Float.fromDpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}

sealed class ToastType {
    object DEFAULT : ToastType()
    object ERROR : ToastType()
}