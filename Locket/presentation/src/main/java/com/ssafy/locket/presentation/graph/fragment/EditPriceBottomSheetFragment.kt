package com.ssafy.locket.presentation.graph.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.locket.CommonUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.locket.presentation.databinding.FragmentEditPriceBottomSheetBinding

class EditPriceBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentEditPriceBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var isFormatting = false

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
        setupNumberFormatting()
        initConfirmButton()
    }

    fun initEvent(){
        binding.btnConfirm.setOnClickListener {

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
                try {
                    isFormatting = true
                    val formatted = CommonUtils.formatNumber(digits)
                    binding.tvWantPrice.setText(formatted)
                    binding.tvWantPrice.setSelection(formatted.length)
                } catch (e: Exception) {
                    // Handle any potential formatting errors
                    e.printStackTrace()
                } finally {
                    isFormatting = false
                }
            }
        })
    }

    private fun initConfirmButton() {
        binding.btnConfirm.setOnClickListener {
            // Get the raw number (without commas)
            val rawNumber = binding.tvWantPrice.text.toString().replace(",", "")
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