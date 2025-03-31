package com.ssafy.locket.presentation.graph.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.locket.presentation.databinding.FragmentEditPriceBottomSheetBinding
import com.ssafy.locket.presentation.graph.viewmodel.EditPriceViewModel
import com.ssafy.locket.presentation.utils.CommonUtils

class EditPriceBottomSheetFragment() : BottomSheetDialogFragment() {
    private var _binding: FragmentEditPriceBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var isFormatting = false
    private val viewModel: EditPriceViewModel by activityViewModels()

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
        initView()
        initEvent()
        setupNumberFormatting()
    }

    fun initView(){
        if(viewModel.editprice.value==""){
            binding.tvWantPrice.hint = "설정 안됨"
        }
        else {
            binding.tvWantPrice.hint = "현재 가격은 "+ CommonUtils.formatNumber(viewModel.editprice.value)+" 원 입니다"
        }
    }


    fun initEvent(){
        binding.btnConfirm.setOnClickListener {
            // Get the raw number (without commas)
            val rawNumber = binding.tvWantPrice.text.toString().replace(",", "")
            viewModel.updatePrice(rawNumber)
            binding.tvWantPrice.setText("")
            dismiss()
        }
    }

    private fun setupNumberFormatting() {
        binding.tvWantPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                val digits = s.toString().replace(Regex("[^\\d]"), "")
                if (digits.isEmpty()) return
                isFormatting = true
                val formatted = CommonUtils.formatNumber(digits)
                binding.tvWantPrice.setText(formatted)
                binding.tvWantPrice.setSelection(formatted.length)
                isFormatting = false
            }
        })
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