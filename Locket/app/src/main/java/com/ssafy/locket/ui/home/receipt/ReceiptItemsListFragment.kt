package com.ssafy.locket.ui.home.receipt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentReceiptBinding
import com.ssafy.locket.databinding.FragmentReceiptItemsListBinding

class ReceiptItemsListFragment : BaseFragment<FragmentReceiptItemsListBinding>(
    FragmentReceiptItemsListBinding::bind,
    R.layout.fragment_receipt_items_list
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}