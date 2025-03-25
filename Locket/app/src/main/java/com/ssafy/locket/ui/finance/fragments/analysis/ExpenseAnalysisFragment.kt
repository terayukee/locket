package com.ssafy.locket.ui.finance.fragments.analysis

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.data.model.dto.CategoryPayment
import com.ssafy.locket.data.model.dto.Receipt
import com.ssafy.locket.databinding.FragmentExpenseAnalysisBinding
import com.ssafy.locket.ui.finance.adapter.CategoryPaymentRVAdapter

class ExpenseAnalysisFragment : BaseFragment<FragmentExpenseAnalysisBinding>(
    FragmentExpenseAnalysisBinding::bind,
    R.layout.fragment_expense_analysis
) {
    private lateinit var categoryPaymentRVAdapter: CategoryPaymentRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        initAdapter()

    }

    private fun initAdapter() {
        categoryPaymentRVAdapter = CategoryPaymentRVAdapter()

        binding.rvCategoryPayment.apply {
            adapter = categoryPaymentRVAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val tmpList : List<CategoryPayment> = listOf(CategoryPayment("shopping",45.2f, 3000), CategoryPayment("cafe",22.2f, 85000))
        categoryPaymentRVAdapter.submitList(tmpList)
    }
}