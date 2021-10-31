package com.example.pokersimulator

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewConfigurationCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokersimulator.databinding.GameBoardFragmentBinding
import com.example.pokersimulator.common.MyCardRecyclerViewAdapter
import com.example.pokersimulator.common.MyOverlapDecorator
import com.example.pokersimulator.common.MyYesNoDialog
import com.example.pokersimulator.listener.*


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
        val cardImageDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.club_1)!!
        val cardWidthHeightRatio = 1.0 * cardImageDrawable.intrinsicWidth / cardImageDrawable.intrinsicHeight
        // The actual width is rounded to the floor so that cards overflows a little to the right hand side
        val actualCardWidth = Math.floor(binding.drawPile.layoutParams.height * cardWidthHeightRatio).toInt()
        // Set the width of the draw pile to almost exactly covers the deck
        binding.drawPile.layoutParams.width = actualCardWidth

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

        // Set the text for your name
        binding.yourName.text = activityViewModel.username

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup the drag and drop listeners
        val dragListener = MyDragListener(viewModel)
        binding.yourHand.setOnDragListener(dragListener)
        binding.drawPile.setOnDragListener(dragListener)
        binding.yourPlayedPile.setOnDragListener(dragListener)

        // Setup the shake listener
        myShakeListener.setOnShakeListener(object : MyShakeListener.OnShakeListener {
            override fun onShake() {
                // TODO add network related stuff
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
        // Setup observer to determine whose turn it is now
        viewModel.currentPlayerLiveData.observe(viewLifecycleOwner) {
            // TODO add network related stuff
            // Setup the force end turn button
            binding.buttonHostPrevilegeAction.apply {
                visibility = if (activityViewModel.isHost && it != "") View.VISIBLE else View.INVISIBLE
                setOnClickListener {
                    MyYesNoDialog(
                        "What do you want to do with the current player?",
                        "Force end",
                        "Kick out",
                        { viewModel.currentPlayerLiveData.value = "" },
                        { viewModel.currentPlayerLiveData.value = "" },
                        {}
                    ).show(parentFragmentManager, null)
                }
            }
            alterTurnBasedFeatures(it)
        }

        // Make the chat log scrollable when overflows
        binding.included.textViewChatLog.movementMethod = ScrollingMovementMethod()
        binding.included.buttonSendMessage.setOnClickListener(
            MySendMessageClickListener(requireContext(), binding.included.editTextChatMessage) {
                if (binding.included.editTextChatMessage.editableText.toString() != "") {
                    binding.included.textViewChatLog.append(activityViewModel.username + ": ")
                    binding.included.textViewChatLog.append(binding.included.editTextChatMessage.editableText)
                    binding.included.textViewChatLog.append("\n")
                    binding.included.editTextChatMessage.editableText.clear()
                }
            }
        )
    }

    private fun alterTurnBasedFeatures(currentPlayer: String) {
        when(currentPlayer) {
            "" -> {
                binding.buttonTurnAction.visibility = View.VISIBLE
                binding.buttonTurnAction.text = "Start turn"
                binding.buttonTurnAction.setOnClickListener {
                    MyYesNoDialog(
                        "Are you sure to START your turn?",
                        "Yes",
                        "No",
                        { viewModel.currentPlayerLiveData.value = activityViewModel.username },
                        {},
                        {}
                    ).show(parentFragmentManager, null)
                }
                MyLongClickListener.isTurn = false
                MyCardClickListener.isTurn = false
            }
            activityViewModel.username -> {
                // TODO use the correct end-turn image resource
                binding.buttonTurnAction.visibility = View.VISIBLE
                binding.buttonTurnAction.text = "End turn"
                binding.buttonTurnAction.setOnClickListener {
                    MyYesNoDialog(
                        "Are you sure to END your turn?",
                        "Yes",
                        "No",
                        { viewModel.currentPlayerLiveData.value = "" },
                        {},
                        {}
                    ).show(parentFragmentManager, null)
                }
                MyLongClickListener.isTurn = true
                MyCardClickListener.isTurn = true
            }
            else -> {
                // TODO use the correct disabled-start-turn image resource (gray out maybe)
                binding.buttonTurnAction.visibility = View.INVISIBLE
                MyLongClickListener.isTurn = false
                MyCardClickListener.isTurn = false
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