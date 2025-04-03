package com.ssafy.locket.presentation.payment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.locket.model.graph.Card
import com.ssafy.locket.model.payment.PaymentCard
import com.ssafy.locket.presentation.R

private const val TAG = "CardAdapter"
class CardAdapter(private var cards: List<PaymentCard>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    private lateinit var context: Context
    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardImageView: ImageView = view.findViewById(R.id.card_image)

        fun bind(item: PaymentCard) {
            Glide.with(context)
                .load(R.drawable.ic_payment_card_img)
                .into(cardImageView)
//            cardImageView.setImageResource(R.drawable.ic_payment_card_img)
            // TODO: 추후 카드에 맞는 이미지 넣기
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount() = cards.size

    fun setCards(cards: List<PaymentCard>) {
        this.cards = cards // 새로운 데이터를 할당
        Log.d(TAG, "setCards: ${cards.size}")
        notifyDataSetChanged()
    }
}
