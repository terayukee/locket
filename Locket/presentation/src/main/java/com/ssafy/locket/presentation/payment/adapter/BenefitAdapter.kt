package com.ssafy.locket.presentation.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.locket.model.payment.Benefit
import com.ssafy.locket.presentation.databinding.ItemBenefitBinding

class BenefitAdapter(private val benefits: List<Benefit>) :
    RecyclerView.Adapter<BenefitAdapter.BenefitViewHolder>() {

    inner class BenefitViewHolder(val binding: ItemBenefitBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BenefitViewHolder {
        val binding = ItemBenefitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BenefitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BenefitViewHolder, position: Int) {
        val benefit = benefits[position]
        holder.binding.tvItem.text = benefit.item
        holder.binding.tvDetail.text = benefit.benefitDetail
    }

    override fun getItemCount(): Int = benefits.size
}