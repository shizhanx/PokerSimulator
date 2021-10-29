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
        with(binding.opponentPlayedPile) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf(), viewModel)
        }
        with(binding.yourHand) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf(), viewModel)
        }
        with(binding.yourPlayedPile) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf(), viewModel)
        }
        with(binding.drawPile) {
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
        // TODO Please use these observers to update the visual effect
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