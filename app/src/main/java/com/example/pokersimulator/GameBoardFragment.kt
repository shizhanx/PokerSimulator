package com.example.pokersimulator

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokersimulator.databinding.GameBoardFragmentBinding
import com.example.pokersimulator.listener.MyDragListener
import com.example.pokersimulator.listener.MyShakeListener
import com.example.pokersimulator.common.MyCardRecyclerViewAdapter
import com.example.pokersimulator.common.MyYesNoDialog


class GameBoardFragment : Fragment() {

    private var _binding: GameBoardFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: GameBoardViewModel
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
        // TODO assign long click listeners to specific cards instead of a pile as a whole
        binding.TEMPDrawPile.setOnLongClickListener {
            // Each ClipData should consist of the following stuff:
            // label: id of the ViewGroup of this card, like draw pile/you hand/etc
            // content type: array of ClipDescription.MIMETYPE_TEXT_PLAIN
            // ClipData.item: the string representation of this card
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

        // Setup the shake listener
        myShakeListener.setOnShakeListener(object : MyShakeListener.OnShakeListener {
            override fun onShake() {
                MyYesNoDialog(
                    getString(R.string.shuffle_draw_pile_confirm),
                    "Yes",
                    "No",
                    { viewModel.shuffleDrawPile() },
                    {},
                ).show(parentFragmentManager, null)
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
            binding.TEMPDrawPile.text = it.count().toString()
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