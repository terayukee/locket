package com.ssafy.locket.presentation.home.notification



import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ssafy.locket.model.home.Notification
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.common.view.MainActivity
import com.ssafy.locket.presentation.databinding.FragmentNotificationBinding
import com.ssafy.locket.presentation.home.character.viewmodel.NotificationState
import com.ssafy.locket.presentation.home.character.viewmodel.NotificationViewModel
import com.ssafy.locket.presentation.home.notification.adapter.NotificationListRVAdapter
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
    
    private val notificationViewModel : NotificationViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        getNotificationData()
        notificationViewModel.getNotifications()
    }

    private fun initAdapter() {
        recentNotificationListRVAdapter = NotificationListRVAdapter()

        binding.rvRecentNotification.apply {
            adapter = recentNotificationListRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        recentNotificationListRVAdapter.itemClickListener = object : NotificationListRVAdapter.ItemClickListener {
            override fun onClick(view: View, data: Notification, position: Int) {
                if(data.type == "goal") (getActivityContext(requireContext()) as MainActivity).setBottomNavigationIndex(R.id.household_account_book)
                else if(data.type == "product") {
                    val bundle = bundleOf("productId" to data.productId)
                    findNavController().navigate(R.id.action_notificationFragment_to_productDetailFragment, bundle)
                }
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

    fun getNotificationData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                notificationViewModel.notificationList.collect { notificationList ->
                    if(notificationList is NotificationState.Success) {
                        val recentNotifications = notificationList.notificationList.recent

                        if(recentNotifications.size == 0) {
                            binding.tvNoNotification.visibility = View.VISIBLE
                            binding.groupRecentNotification.visibility = View.GONE
                        } else {
                            binding.tvNoNotification.visibility = View.GONE
                            binding.groupRecentNotification.visibility = View.VISIBLE
                        }
                        recentNotificationListRVAdapter.submitList(recentNotifications)
                    }
                }
            }
        }
    }
}