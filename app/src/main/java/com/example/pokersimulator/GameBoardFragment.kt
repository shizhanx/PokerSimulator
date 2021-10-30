package com.example.pokersimulator

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewConfigurationCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokersimulator.databinding.GameBoardFragmentBinding
import com.example.pokersimulator.listener.MyDragListener
import com.example.pokersimulator.listener.MyShakeListener
import com.example.pokersimulator.common.MyCardRecyclerViewAdapter
import com.example.pokersimulator.common.MyOverlapDecorator
import com.example.pokersimulator.common.MyYesNoDialog
import com.example.pokersimulator.listener.MyCardClickListener
import com.example.pokersimulator.listener.MyLongClickListener


class GameBoardFragment : Fragment() {

    private var _binding: GameBoardFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: GameBoardViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var sensorManager: SensorManager
    private var mLinearAccelerometer: Sensor? = null
    private val myShakeListener = MyShakeListener()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameBoardFragmentBinding.inflate(inflater, container, false)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mLinearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        // Calculate the actual width of the card images showing on the screen for overlap decorator
        val cardImageDrawable = resources.getDrawable(R.drawable.club_1, null)
        val cardWidthHeightRatio = 1.0 * cardImageDrawable.intrinsicWidth / cardImageDrawable.intrinsicHeight
        // The actual width is rounded to the floor so that cards overflows a little to the right hand side
        val actualCardWidth = Math.floor(binding.drawPile.layoutParams.height * cardWidthHeightRatio).toInt()
        // Set the width of the draw pile to almost exactly covers the deck
        binding.drawPile.layoutParams.width = actualCardWidth + 50

        // Setup the recycler views
        binding.opponentPlayedPile.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf(), viewModel)
        }
        binding.yourHand.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf(), viewModel)
        }
        binding.yourPlayedPile.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf(), viewModel)
        }
        binding.drawPile.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf(), viewModel)
            addItemDecoration(MyOverlapDecorator(actualCardWidth))
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Setup the drag and drop listeners
        val dragListener = MyDragListener(viewModel)
        binding.yourHand.setOnDragListener(dragListener)
        binding.drawPile.setOnDragListener(dragListener)
        binding.yourPlayedPile.setOnDragListener(dragListener)

        // Setup the shake listener
        myShakeListener.setOnShakeListener(object : MyShakeListener.OnShakeListener {
            override fun onShake() {
                sensorManager.unregisterListener(myShakeListener)
                val myYesNoDialog = MyYesNoDialog(
                    getString(R.string.shuffle_draw_pile_confirm),
                    "Yes",
                    "No",
                    { viewModel.shuffleDrawPile() },
                    {},
                    { sensorManager.registerListener(myShakeListener, mLinearAccelerometer, SensorManager.SENSOR_DELAY_GAME) }
                )
                myYesNoDialog.show(parentFragmentManager, null)
            }
        })
        sensorManager.registerListener(myShakeListener, mLinearAccelerometer, SensorManager.SENSOR_DELAY_GAME)

        // Setup the observers that reacts to changes in the pile data in viewModel
        viewModel.yourHandLiveData.observe(viewLifecycleOwner, {
            val adapter = binding.yourHand.adapter as MyCardRecyclerViewAdapter
            adapter.updatePile(it.toList())
        })
        viewModel.opponentPlayedPileLiveData.observe(viewLifecycleOwner, {
            val adapter = binding.opponentPlayedPile.adapter as MyCardRecyclerViewAdapter
            adapter.updatePile(it.toList())
        })
        viewModel.yourPlayedPileLiveData.observe(viewLifecycleOwner, {
            val adapter = binding.yourPlayedPile.adapter as MyCardRecyclerViewAdapter
            adapter.updatePile(it.toList())
        })
        viewModel.drawPileLiveData.observe(viewLifecycleOwner, {
            val adapter = binding.drawPile.adapter as MyCardRecyclerViewAdapter
            adapter.updatePile(it.toList())
            binding.numberCardsInDrawPile.text = getString(R.string.number_cards_in_draw_pile, it.size)
        })
        // Setup the force end turn button
        binding.buttonForceEndTurn.apply {
            visibility = if (activityViewModel.isHost) View.VISIBLE else View.INVISIBLE
            setOnClickListener {
                if (viewModel.currentPlayerLiveData.value != "")
                    viewModel.currentPlayerLiveData.value = ""
            }
        }
        // Setup observer to determine whose turn it is now
        viewModel.currentPlayerLiveData.observe(viewLifecycleOwner) {
            when(it) {
                "" -> {
                    binding.buttonTurnAction.visibility = View.VISIBLE
                    binding.buttonTurnAction.text = "Start turn"
                    binding.buttonTurnAction.setOnClickListener {
                        viewModel.currentPlayerLiveData.value = activityViewModel.username
                    }
                    MyLongClickListener.isTurn = false
                    MyCardClickListener.isTurn = false
                    binding.textViewCurrentPlayer.text = getString(R.string.current_player_name, "no one")
                }
                activityViewModel.username -> {
                    // TODO use the correct end-turn image resource
                    binding.buttonTurnAction.visibility = View.VISIBLE
                    binding.buttonTurnAction.text = "End turn"
                    binding.buttonTurnAction.setOnClickListener {
                        viewModel.currentPlayerLiveData.value = ""
                    }
                    MyLongClickListener.isTurn = true
                    MyCardClickListener.isTurn = true
                    binding.textViewCurrentPlayer.text = getString(R.string.current_player_name, "You")
                }
                else -> {
                    // TODO use the correct disabled-start-turn image resource (gray out maybe)
                    binding.buttonTurnAction.visibility = View.INVISIBLE
                    MyLongClickListener.isTurn = false
                    MyCardClickListener.isTurn = false
                    binding.textViewCurrentPlayer.text =
                        getString(R.string.current_player_name, viewModel.currentPlayerLiveData.value)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(myShakeListener, mLinearAccelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        sensorManager.unregisterListener(myShakeListener)
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}