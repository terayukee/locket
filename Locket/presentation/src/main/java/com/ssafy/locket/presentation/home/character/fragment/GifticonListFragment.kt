package com.ssafy.locket.presentation.home.character.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentGifticonListBinding
import com.ssafy.locket.presentation.home.character.adapter.GifticonRVAdapter
import com.ssafy.locket.presentation.home.character.viewmodel.GifticonState
import com.ssafy.locket.presentation.home.character.viewmodel.GifticonsViewModel
import com.ssafy.locket.presentation.utils.CommonUtils.fromDpToPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "GifticonListFragment"
@AndroidEntryPoint
class GifticonListFragment : BaseFragment<FragmentGifticonListBinding>(
    FragmentGifticonListBinding::bind,
    R.layout.fragment_gifticon_list
) {
    private val gifticonViewModel: GifticonsViewModel by viewModels()
    private lateinit var gifticonRVAdapter: GifticonRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initUI()
    }

    private fun initUI() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        gifticonViewModel.getAllGifticons()
        viewLifecycleOwner.lifecycleScope.launch {
            gifticonViewModel.gifticonList.collect { uiState ->
                when(uiState) {
                    is GifticonState.Success -> {
                        if (uiState.gifticonList.gifticons.isEmpty()) binding.tvEmptyList.visibility = View.VISIBLE
                        else binding.tvEmptyList.visibility = View.GONE
                        gifticonRVAdapter.submitList(uiState.gifticonList.gifticons)
                    }
                    is GifticonState.Error -> {
                        Log.d(TAG, "initUI: ${uiState.message}")
                    }
                    is GifticonState.Loading -> {
                        Log.d(TAG, "initUI: Loading")
                    }
                    else -> {
                        Log.d(TAG, "initUI: else")
                    }
                }

            }
        }
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
                findNavController().navigate(R.id.action_gifticonListFragment_to_characterDoneFragment)
            }
        }
    }
}