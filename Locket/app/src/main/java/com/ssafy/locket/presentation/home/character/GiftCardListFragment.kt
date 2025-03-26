package com.ssafy.locket.presentation.home.character

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentGiftCardListBinding


class GiftCardListFragment : BaseFragment<FragmentGiftCardListBinding>(
    FragmentGiftCardListBinding::bind,
    R.layout.fragment_gift_card_list
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}