package com.ssafy.locket.presentation.home.notification

import android.content.Context
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
import com.ssafy.locket.model.home.character.Gifticon
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.common.view.MainActivity
import com.ssafy.locket.presentation.common.viewmodel.FinanceNavigationState
import com.ssafy.locket.presentation.databinding.FragmentNotificationBinding
import com.ssafy.locket.presentation.graph.viewmodel.ProductHappyListState
import com.ssafy.locket.presentation.home.character.adapter.GifticonRVAdapter
import com.ssafy.locket.presentation.home.character.viewmodel.NotificationState
import com.ssafy.locket.presentation.home.character.viewmodel.NotificationViewModel
import com.ssafy.locket.presentation.home.notification.adapter.NotificationListRVAdapter
import com.ssafy.locket.presentation.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.ViewComponentManager
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

        recentNotificationListRVAdapter.itemClickListener = object : NotificationListRVAdapter.ItemClickListener {
            override fun onClick(view: View, data: Notification, position: Int) {
                if(data.type == "goal") (getActivityContext(requireContext()) as MainActivity).setBottomNavigationIndex(R.id.household_account_book)
                else if(data.type == "product") (getActivityContext(requireContext()) as MainActivity).setBottomNavigationIndex(R.id.lowest_price_graph)
            }
        }

        prevNotificationListRVAdapter.itemClickListener = object : NotificationListRVAdapter.ItemClickListener {
            override fun onClick(view: View, data: Notification, position: Int) {
                if(data.type == "goal") (getActivityContext(requireContext()) as MainActivity).setBottomNavigationIndex(R.id.household_account_book)
                else if(data.type == "product") (getActivityContext(requireContext()) as MainActivity).setBottomNavigationIndex(R.id.lowest_price_graph)
            }
        }
    }

    fun getActivityContext(context: Context): Context {
        return if (context is ViewComponentManager.FragmentContextWrapper) {
            context.baseContext
        } else {
            context
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

                        if(recentNotifications.size > 0) binding.groupRecentNotification.visibility = View.VISIBLE
                        if(pastNotifications.size > 0) binding.groupPrevNotification.visibility = View.VISIBLE

                        recentNotificationListRVAdapter.submitList(recentNotifications)
                        prevNotificationListRVAdapter.submitList(pastNotifications)
                    }
                }
            }
        }
    }
}