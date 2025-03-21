package com.ssafy.locket.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivCharacterBg.setOnClickListener {
            findNavController().navigate(R.id.characterFragment)
        }

        binding.ivReceiptBg.setOnClickListener {
            findNavController().navigate(R.id.receiptListFragment)
        }

        binding.icNotification.setOnClickListener {
            findNavController().navigate(R.id.notificationFragment)
        }
    }
}