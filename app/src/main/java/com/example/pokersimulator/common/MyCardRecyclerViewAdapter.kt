package com.example.pokersimulator.common

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokersimulator.GameBoardViewModel
import com.example.pokersimulator.R
import com.example.pokersimulator.databinding.CardFragmentBinding
import com.example.pokersimulator.domain_object.CardData
import com.example.pokersimulator.listener.MyCardClickListener
import com.example.pokersimulator.listener.MyLongClickListener
import kotlin.properties.Delegates

/**
 * Adaptor for the recycler view of a pile of cards that can be shown as a list to the user
 */
class MyCardRecyclerViewAdapter(
    var pile: List<CardData>,
    val viewModel: GameBoardViewModel,
    val logZone: TextView
)
    : RecyclerView.Adapter<MyCardRecyclerViewAdapter.ViewHolder>() {

    private var parentViewId by Delegates.notNull<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        parentViewId = recyclerView.id
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = pile[position]
        //holder.cardInfoView.text = card.toString()

        val context: Context = holder.cardInfoView.context
        val id: Int = if (card.faceUp)
            context.resources.getIdentifier(card.toString(), "drawable", context.packageName)
        else
            context.resources.getIdentifier("back_side", "drawable", context.packageName)
        holder.cardInfoView.setImageResource(id)
        holder.setListeners(position)
    }

    override fun getItemCount(): Int = pile.size

    fun updatePile(newPile: List<CardData>) {
        pile = newPile
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: CardFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
        val cardInfoView = binding.imageViewCard

        fun setListeners(position: Int) {
            itemView.setOnLongClickListener(MyLongClickListener(parentViewId, position.toString()))
            itemView.setOnClickListener(MyCardClickListener(viewModel, parentViewId, position, logZone))
        }
    }

}