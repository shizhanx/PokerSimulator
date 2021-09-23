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

class GameBoardFragment : Fragment() {

    private var _binding: GameBoardFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: GameBoardViewModel

    private val dragListener = View.OnDragListener{ view, dragEvent ->
        var gameActionType: GameActionEnum? = null
        // Determine the possible game play option of this drag and drop action
        if (dragEvent.clipDescription != null) {
            if (view.id == R.id.your_hand && dragEvent.clipDescription.label == R.id.TEMP_draw_pile.toString()) {
                gameActionType = GameActionEnum.DRAW
            } else if (view.id == R.id.TEMP_draw_pile && dragEvent.clipDescription.label == R.id.your_hand.toString()) {
                gameActionType = GameActionEnum.UNDO_DRAW
            } else if (view.id == R.id.your_played_pile && dragEvent.clipDescription.label == R.id.your_hand.toString()) {
                gameActionType = GameActionEnum.PLAY
            } else if (view.id == R.id.your_hand && dragEvent.clipDescription.label == R.id.your_played_pile.toString()) {
                gameActionType = GameActionEnum.UNDO_PLAY
            }
        } else Log.d("TAG", ": null clipdescription, ${dragEvent.action == DragEvent.ACTION_DRAG_ENDED}")

        // TODO Add visual effect to highlight entering and exiting a droppable zone
        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                Log.d("TAG", ": started!")
                gameActionType != null
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.d("TAG", ": entered!$gameActionType")
                gameActionType != null
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                gameActionType != null
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                gameActionType != null
            }
            DragEvent.ACTION_DROP -> {
                when (gameActionType) {
                    GameActionEnum.DRAW -> {
                        viewModel.drawCard()
                    }
                    // TODO finish the rest of actions
                }
                gameActionType != null
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                gameActionType != null
            }
            else -> false
        }
    }

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

        // Setup the drag and drop listeners
        // TODO set listener on other piles as well
        binding.yourHand.setOnDragListener(dragListener)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameBoardViewModel::class.java)
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