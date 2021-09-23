package com.example.pokersimulator

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokersimulator.databinding.GameBoardFragmentBinding
import com.example.pokersimulator.databinding.IndexPageFragmentBinding
import com.example.pokersimulator.databinding.RoomFragmentBinding
import com.example.pokersimulator.domain_object.CardData
import com.example.pokersimulator.domain_object.CardType

class GameBoardFragment : Fragment() {

    private var _binding: GameBoardFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: GameBoardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = GameBoardFragmentBinding.inflate(inflater, container, false)

        // Setup the recycler views
        // TODO get rid of card coded pile for testing
        val TEMP_pile = listOf(CardData(CardType.JOKER, 1), CardData(CardType.JOKER, 2))
        with(binding.opponentPlayedPile) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(TEMP_pile.map { it.copy() }.toList())
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameBoardViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}