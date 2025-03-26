package com.ssafy.locket.presentation.notification

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.data.model.dto.Notification
import com.ssafy.locket.databinding.FragmentNotificationBinding
import com.ssafy.locket.presentation.notification.adapter.NotificationListRVAdapter

private const val TAG = "NotificationFragment"
class NotificationFragment : BaseFragment<FragmentNotificationBinding>(
    FragmentNotificationBinding::bind,
    R.layout.fragment_notification
) {
    private lateinit var recentNotificationListRVAdapter: NotificationListRVAdapter
    private lateinit var prevNotificationListRVAdapter: NotificationListRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
        recentNotificationListRVAdapter = NotificationListRVAdapter()
        prevNotificationListRVAdapter = NotificationListRVAdapter()

        binding.rvRecentNotification.apply {
            adapter = recentNotificationListRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.rvPrevNotification.apply {
            adapter = prevNotificationListRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val tmpRecentList : List<Notification> = listOf(Notification(0,"product","최저가 알림","당근이 설정하신 가격보다 내려갔습니다. 사이트로 들어가 확인하세요","3월 21일"),Notification(1,"product","최저가 알림","신발이 설정하신 가격보다 내려갔습니다. 사이트로 들어가 확인하세요","3월 20일"))
        recentNotificationListRVAdapter.submitList(tmpRecentList)

        val tmpPrevList : List<Notification> = listOf(Notification(0,"product","최저가 알림","당근이 설정하신 가격보다 내려갔습니다. 사이트로 들어가 확인하세요","3월 21일"),Notification(1,"product","최저가 알림","신발이 설정하신 가격보다 내려갔습니다. 사이트로 들어가 확인하세요","3월 20일"))
        prevNotificationListRVAdapter.submitList(tmpPrevList)
    }
}