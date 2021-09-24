package com.example.pokersimulator.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokersimulator.databinding.CardFragmentBinding
import com.example.pokersimulator.domain_object.CardData

/**
 * Adaptor for the recycler view of a pile of cards that can be shown as a list to the user
 */
class MyCardRecyclerViewAdapter(
    var pile: List<CardData>
)
    : RecyclerView.Adapter<MyCardRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = pile[position]
        holder.cardInfoView.text = card.toString()
    }

    override fun getItemCount(): Int = pile.size

    fun updatePile(newPile: List<CardData>) {
        pile = newPile
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: CardFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
        val cardInfoView = binding.cardInfo
    }

}