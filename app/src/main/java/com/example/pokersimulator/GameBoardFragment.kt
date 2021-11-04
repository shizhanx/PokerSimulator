package com.example.pokersimulator

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.ActivityChooserView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokersimulator.databinding.GameBoardFragmentBinding
import com.example.pokersimulator.common.MyCardRecyclerViewAdapter
import com.example.pokersimulator.common.MyOverlapDecorator
import com.example.pokersimulator.common.MyYesNoDialog
import com.example.pokersimulator.domain_object.CardData
import com.example.pokersimulator.listener.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


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

    private val database = Firebase.database("https://mystical-binder-330900-default-rtdb.asia-southeast1.firebasedatabase.app/")

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
            adapter = MyCardRecyclerViewAdapter(listOf(), viewModel, binding.includeChatLogFragment.textViewChatLog)
        }
        binding.yourHand.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf(), viewModel, binding.includeChatLogFragment.textViewChatLog)
        }
        binding.yourPlayedPile.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf(), viewModel, binding.includeChatLogFragment.textViewChatLog)
        }
        binding.drawPile.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MyCardRecyclerViewAdapter(listOf(), viewModel, binding.includeChatLogFragment.textViewChatLog)
            addItemDecoration(MyOverlapDecorator(actualCardWidth))
        }

        // Set the text for your name
        binding.includeUserFragment.textViewUsername.text = activityViewModel.username
        // Set up the menu for sorting options next to your name
        binding.includeUserFragment.buttonUserAction.text = "Sort by"
        val sortingMenu = PopupMenu(requireContext(), binding.includeUserFragment.buttonUserAction).apply {
            inflate(R.menu.menu_sorting_options)
            setOnMenuItemClickListener {
                when (it.title) {
                    getString(R.string.sort_by_type) -> {
                        viewModel.sortYourHand(CardData.comparatorByType)
                        true
                    }
                    getString(R.string.sort_by_number) -> {
                        viewModel.sortYourHand(CardData.comparatorByNumber)
                        true
                    }
                    else -> false
                }
            }
        }
        binding.includeUserFragment.buttonUserAction.setOnClickListener {
            sortingMenu.show()
        }

        // Set the initial game instructions
        binding.includeChatLogFragment.textViewChatLog.append(getString(R.string.game_instruction))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup the drag and drop listeners
        val dragListener = MyDragListener(viewModel, binding.includeChatLogFragment.textViewChatLog)
        binding.yourHand.setOnDragListener(dragListener)
        binding.drawPile.setOnDragListener(dragListener)
        binding.yourPlayedPile.setOnDragListener(dragListener)

        // Setup the shake listener
        myShakeListener.setOnShakeListener(object : MyShakeListener.OnShakeListener {
            override fun onShake() {
                // Only allow dialog when cover image is not showing.
                // This check is theoretically redundant as sensor listeners are removed when cover image shows
                if (activityViewModel.isCoverImageShowing.value != true) {
                    // TODO add network related stuff
                    sensorManager.unregisterListener(myShakeListener)
                    MyYesNoDialog(
                        getString(R.string.shuffle_draw_pile_confirm),
                        "Yes",
                        "No",
                        { binding.includeChatLogFragment.textViewChatLog.append(viewModel.currentPlayerLiveData.value + viewModel.shuffleDrawPile()) },
                        {},
                        {
                            sensorManager.registerListener(
                                myShakeListener,
                                mLinearAccelerometer,
                                SensorManager.SENSOR_DELAY_GAME
                            )
                        }
                    ).show(childFragmentManager, null)
                }
            }
        })

        // Hide any active dialog and remove sensor listener when the covering image shows up
        activityViewModel.isCoverImageShowing.observe(viewLifecycleOwner) {
            if (it) {
                for (fragment in childFragmentManager.fragments) {
                    if (fragment is DialogFragment) fragment.dismiss()
                }
                sensorManager.unregisterListener(myShakeListener)
            } else sensorManager.registerListener(myShakeListener, mLinearAccelerometer, SensorManager.SENSOR_DELAY_GAME)
        }

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
                        {
                            viewModel.currentPlayerLiveData.value = ""
                            val currentPlayerRef = database.getReference(activityViewModel.roomPath + "/players")
                            currentPlayerRef.setValue(viewModel.currentPlayerLiveData.value)
                            binding.includeChatLogFragment.textViewChatLog.append("The host force ended this turn\n")
                        },
                        {
                            viewModel.currentPlayerLiveData.value = ""
                            binding.includeChatLogFragment.textViewChatLog.append("The host kicked out ${viewModel.currentPlayerLiveData.value}\n")
                        },
                        {}
                    ).show(childFragmentManager, null)
                }
            }
            alterTurnBasedFeatures(it)
        }

        // Make the chat log scrollable when overflows
        binding.includeChatLogFragment.textViewChatLog.movementMethod = ScrollingMovementMethod()
        binding.includeChatLogFragment.buttonSendMessage.setOnClickListener(
            MySendMessageClickListener(requireContext(), binding.includeChatLogFragment.editTextChatMessage) {
                if (binding.includeChatLogFragment.editTextChatMessage.editableText.toString() != "") {
                    //TODO Network: send messages online
                    binding.includeChatLogFragment.textViewChatLog.append(activityViewModel.username + ": ")
                    binding.includeChatLogFragment.textViewChatLog.append(binding.includeChatLogFragment.editTextChatMessage.editableText)
                    binding.includeChatLogFragment.textViewChatLog.append("\n")
                    binding.includeChatLogFragment.editTextChatMessage.editableText.clear()
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
                        {
                            viewModel.currentPlayerLiveData.value = activityViewModel.username

                            val currentPlayerRef = database.getReference(activityViewModel.roomPath + "/currentPlayer")
                            val lastTurnRef = database.getReference(activityViewModel.roomPath + "/lastTurn")

                            currentPlayerRef.setValue(viewModel.currentPlayerLiveData.value)
                            lastTurnRef.setValue(viewModel.currentPlayerLiveData.value)
                            binding.includeChatLogFragment.textViewChatLog.append("${viewModel.currentPlayerLiveData.value}'s turn just started\n")
                        },
                        {},
                        {}
                    ).show(childFragmentManager, null)
                }
                MyLongClickListener.isTurn = false
                MyCardClickListener.isTurn = false
            }
            activityViewModel.username -> {
                binding.buttonTurnAction.visibility = View.VISIBLE
                binding.buttonTurnAction.text = "End turn"
                binding.buttonTurnAction.setOnClickListener {
                    MyYesNoDialog(
                        "Are you sure to END your turn?",
                        "Yes",
                        "No",
                        {
                            binding.includeChatLogFragment.textViewChatLog.append("${viewModel.currentPlayerLiveData.value}'s turn just ended\n")
                            viewModel.currentPlayerLiveData.value = ""
                            val currentPlayerRef = database.getReference(activityViewModel.roomPath + "/currentPlayer")
                            currentPlayerRef.setValue(viewModel.currentPlayerLiveData.value)
                            val playerHandDataRef = database.getReference(activityViewModel.roomPath + "/players/" + activityViewModel.username + "/HandData")
                            playerHandDataRef.setValue(viewModel.yourHandLiveData)
                            val playerPlayedPileDataRef = database.getReference(activityViewModel.roomPath + "/players/" + activityViewModel.username + "/PlayedPileData")
                            playerPlayedPileDataRef.setValue(viewModel.yourPlayedPileLiveData)
                            val drawPileDataRef = database.getReference(activityViewModel.roomPath + "/drawPileData")
                            drawPileDataRef.setValue(viewModel.drawPileLiveData)
                            },
                        {},
                        {}
                    ).show(childFragmentManager, null)
                }
                MyLongClickListener.isTurn = true
                MyCardClickListener.isTurn = true
            }
            else -> {
                binding.buttonTurnAction.visibility = View.INVISIBLE
                MyLongClickListener.isTurn = false
                MyCardClickListener.isTurn = false
            }
        }

        val currentPlayerRef = database.getReference(activityViewModel.roomPath)
        val currentPlayerListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val currentTurn = dataSnapshot.child("currentPlayer").getValue().toString()
                    if (currentTurn.isNullOrEmpty()){
                        val lastTurnPlayer = dataSnapshot.child("lastTurn").getValue().toString()
                        if (lastTurnPlayer != activityViewModel.username){
                            val opponentPlayedlist : MutableList<CardData> = mutableListOf()
                            val opponentPlayedChildren = dataSnapshot.child("players").child(lastTurnPlayer).child("PlayedPileData").child("value")!!.children
                            opponentPlayedChildren.forEach {
                                it.getValue(CardData::class.java)?.let { it1 -> opponentPlayedlist.add(it1) }
                                viewModel.opponentPlayedPileLiveData.value = opponentPlayedlist
                            }

                            val drawPilelist : MutableList<CardData> = mutableListOf()
                            val drawPileChildren = dataSnapshot.child("drawPileData").child("value")!!.children
                            drawPileChildren.forEach {
                                it.getValue(CardData::class.java)?.let { it1 -> drawPilelist.add(it1) }
                                viewModel.drawPileLiveData.value = drawPilelist
                                Log.w("Cards: ", drawPilelist.toString())
                            }
                        }
                    }
                } else {
                    currentPlayerRef.removeEventListener(this)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("loadPost:onCancelled", databaseError.toException())
            }
        }
        currentPlayerRef.addValueEventListener(currentPlayerListener)

    }



    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(myShakeListener, mLinearAccelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(myShakeListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (activityViewModel.isHost) {
            val roomRef = database.getReference(activityViewModel.roomPath)
            roomRef.removeValue()
        }
    }
}