package com.example.pokersimulator

import android.content.ClipData
import android.content.ClipDescription
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokersimulator.databinding.GameBoardFragmentBinding
import com.example.pokersimulator.domain_object.GameActionEnum
import com.example.pokersimulator.listener.MyDragListener

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

        // Setup the long click listeners for drag and drop
        binding.TEMPDrawPile.setOnLongClickListener {
            val clipData = ClipData(
                R.id.TEMP_draw_pile.toString(),
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                ClipData.Item(R.id.TEMP_draw_pile.toString())
            )
            it.startDragAndDrop(clipData, View.DragShadowBuilder(it), null, 0)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameBoardViewModel::class.java)

        // Setup the drag and drop listeners
        // TODO set listener on other piles as well
        binding.yourHand.setOnDragListener(MyDragListener(viewModel))

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