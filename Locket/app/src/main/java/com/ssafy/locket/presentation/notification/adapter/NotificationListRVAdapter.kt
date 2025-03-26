package com.ssafy.locket.presentation.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.locket.R
import com.ssafy.locket.data.model.dto.Notification
import com.ssafy.locket.databinding.ItemNotificationBinding

class NotificationListRVAdapter:
    ListAdapter<Notification, NotificationListRVAdapter.CustomViewHolder>(CustomComparator) {
    private lateinit var context: Context

    companion object CustomComparator : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }

    inner class CustomViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notification) {
            val notificationImg : Int = when(item.type) {
                "product" -> R.drawable.ic_notification_product
                "finance" -> R.drawable.ic_notification_finance
                else -> R.drawable.ic_finance_category_etc
            }

            Glide.with(context)
                .load(notificationImg)
                .placeholder(R.drawable.ic_finance_category_etc)
                .into(binding.ivNotification)
            binding.tvNotificationTitle.text = item.title
            binding.tvNotificationContent.text = item.content
            binding.tvNotificationDate.text = item.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context = parent.context
        val binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(context), parent, false)

//        val displayMetrics = context.resources.displayMetrics
//        val screenHeight = displayMetrics.heightPixels
//        val itemHeight = (screenHeight * 0.08).toInt()
//        binding.root.layoutParams.height = itemHeight
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
