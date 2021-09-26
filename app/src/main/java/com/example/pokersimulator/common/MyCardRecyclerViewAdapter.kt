package com.example.pokersimulator.common

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokersimulator.R
import com.example.pokersimulator.databinding.CardFragmentBinding
import com.example.pokersimulator.domain_object.CardData
import kotlin.properties.Delegates

/**
 * Adaptor for the recycler view of a pile of cards that can be shown as a list to the user
 */
class MyCardRecyclerViewAdapter(
    var pile: List<CardData>
)
    : RecyclerView.Adapter<MyCardRecyclerViewAdapter.ViewHolder>() {

    private var parentViewId by Delegates.notNull<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        parentViewId = recyclerView.id
        Log.d("TAG", "onAttachedToRecyclerView: ${parentViewId == R.id.your_played_pile}")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = pile[position]
        holder.cardInfoView.text = card.toString()
        holder.setListeners(card)
    }

    override fun getItemCount(): Int = pile.size

    fun updatePile(newPile: List<CardData>) {
        pile = newPile
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: CardFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
        // TODO UI add graphical representation of each card so that moving the card is more enjoyable
        val cardInfoView = binding.cardInfo

        fun setListeners(cardData: CardData) {
            itemView.setOnLongClickListener(MyLongClickListener(parentViewId, cardData.toString()))
        }
    }

}