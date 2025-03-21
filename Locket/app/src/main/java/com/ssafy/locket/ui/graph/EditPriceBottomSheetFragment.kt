package com.ssafy.locket.ui.graph

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentDeleteAccountBinding
import com.ssafy.locket.databinding.FragmentEditBudgetBinding
import com.ssafy.locket.databinding.FragmentEditPriceBottomSheetBinding

class EditPriceBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentEditPriceBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPriceBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    private fun initEvent() {
        binding.btnConfirm.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "EditPriceBottomSheetFragment"
        fun newInstance(): EditPriceBottomSheetFragment {
            return EditPriceBottomSheetFragment()
        }
    }
}