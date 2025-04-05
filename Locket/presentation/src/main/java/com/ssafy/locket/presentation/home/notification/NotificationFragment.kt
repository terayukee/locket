package com.ssafy.locket.presentation.home.notification

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.home.Notification
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentNotificationBinding
import com.ssafy.locket.presentation.graph.viewmodel.ProductHappyListState
import com.ssafy.locket.presentation.home.character.viewmodel.NotificationState
import com.ssafy.locket.presentation.home.character.viewmodel.NotificationViewModel
import com.ssafy.locket.presentation.home.notification.adapter.NotificationListRVAdapter
import com.ssafy.locket.presentation.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val TAG = "NotificationFragment"
@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>(
    FragmentNotificationBinding::bind,
    R.layout.fragment_notification
) {
    private lateinit var recentNotificationListRVAdapter: NotificationListRVAdapter
    private lateinit var prevNotificationListRVAdapter: NotificationListRVAdapter
    
    private val notificationViewModel : NotificationViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        getNotificationData()
        notificationViewModel.getNotifications()
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

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun getNotificationData(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                notificationViewModel.notificationList.collect { notificationList ->
                    Log.d(TAG,"확인"+notificationList)
                    if(notificationList is NotificationState.Success) {
                        val pastNotifications = notificationList.notificationList.past
                        val recentNotifications = notificationList.notificationList.recent
                        // ✅ RecyclerView 업데이트
                        recentNotificationListRVAdapter.submitList(recentNotifications)
                        prevNotificationListRVAdapter.submitList(pastNotifications)

                    }
                }
            }
        }
    }
}