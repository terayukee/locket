package com.ssafy.locket.presentation.home.character.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locket.CommonUtils.fromDpToPx
import com.ssafy.locket.model.finance.Payment
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentGiftCardListBinding
import com.ssafy.locket.presentation.finance.adapter.PaymentRVAdapter
import com.ssafy.locket.presentation.home.character.adapter.GifticonRVAdapter


class GiftCardListFragment : BaseFragment<FragmentGiftCardListBinding>(
    FragmentGiftCardListBinding::bind,
    R.layout.fragment_gift_card_list
) {
    private lateinit var gifticonRVAdapter: GifticonRVAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    private fun initAdapter() {
        gifticonRVAdapter = GifticonRVAdapter()

        binding.rvGifticon.apply {
            adapter = gifticonRVAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(GifticonRVAdapter.GridSpacingItemDecoration(2, 8f.fromDpToPx()))
        }

        gifticonRVAdapter.itemClickListener = object : GifticonRVAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int) {
                findNavController().navigate(R.id.action_giftCardListFragment_to_characterDoneFragment)
            }
        }

        val tmpList : List<String> = listOf(
            "소심냥",
            "반짝 소심냥",
            "반짝반짝 소심냥",
            "소심한냥",
            "반짝 소심한냥"
        )
        
        gifticonRVAdapter.submitList(tmpList)
    }
}