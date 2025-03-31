package com.ssafy.locket.presentation.home.character.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.locket.presentation.databinding.ItemGifticonBinding

class GifticonRVAdapter: ListAdapter<String, GifticonRVAdapter.CustomViewHolder>(
    GifticonRVAdapter
)  {
    lateinit var itemClickListener: ItemClickListener
    private lateinit var context: Context

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    companion object CustomComparator : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    inner class CustomViewHolder(private val binding: ItemGifticonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.root.setOnClickListener{
                itemClickListener.onClick(it, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GifticonRVAdapter.CustomViewHolder {
        context = parent.context
        val binding = ItemGifticonBinding.inflate(LayoutInflater.from(context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GifticonRVAdapter.CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    internal class GridSpacingItemDecoration(
        private val spanCount: Int, // Grid의 column 수
        private val spacing: Int // 간격
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position: Int = parent.getChildAdapterPosition(view)

            if (position >= 0) {
                val column = position % spanCount // item column
                outRect.apply {
                    left = spacing - column * spacing / spanCount
                    if(column == 1) right = spacing
                    else right = (column + 1) * spacing / spanCount
                    if (position < spanCount) top = spacing
                    bottom = spacing
                }
            } else {
                outRect.apply {
                    left = 0
                    right = 0
                    top = 0
                    bottom = 0
                }
            }
        }
    }
}