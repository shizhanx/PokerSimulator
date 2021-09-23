package com.example.pokersimulator

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokersimulator.databinding.GameBoardFragmentBinding

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
//        val TEMP_pile = listOf(CardData(CardType.JOKER, 1), CardData(CardType.JOKER, 2), CardData(CardType.JOKER, 2), CardData(CardType.JOKER, 2), CardData(CardType.JOKER, 2), CardData(CardType.JOKER, 2), CardData(CardType.JOKER, 2), CardData(CardType.JOKER, 2), CardData(CardType.JOKER, 2), CardData(CardType.JOKER, 2), CardData(CardType.JOKER, 2))
        with(binding.opponentPlayedPile) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf())
        }
        with(binding.yourHand) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf())
        }
        with(binding.yourPlayedPile) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf())
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameBoardViewModel::class.java)
        // TODO: Use the ViewModel
        viewModel.yourHandLiveData.observe(viewLifecycleOwner, Observer {
            val adapter = binding.yourHand.adapter as MyCardRecyclerViewAdapter
            adapter.updatePile(it.toList())
        })
        viewModel.opponentPlayedPileLiveData.observe(viewLifecycleOwner, Observer {
            val adapter = binding.opponentPlayedPile.adapter as MyCardRecyclerViewAdapter
            adapter.updatePile(it.toList())
        })
        viewModel.yourPlayedPileLiveData.observe(viewLifecycleOwner, Observer {
            val adapter = binding.yourPlayedPile.adapter as MyCardRecyclerViewAdapter
            adapter.updatePile(it.toList())
        })
        viewModel.drawPileLiveData.observe(viewLifecycleOwner, Observer {
            binding.TEMPDrawPile.text = it.count().toString()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}