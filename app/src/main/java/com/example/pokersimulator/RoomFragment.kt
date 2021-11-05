package com.example.pokersimulator

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokersimulator.MainActivity.Companion.database
import com.example.pokersimulator.common.MyUsernameRecyclerViewAdapter
import com.example.pokersimulator.databinding.RoomFragmentBinding
import com.example.pokersimulator.listener.MySendMessageClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 * A fragment for users joined the same host to see each other and prepare for the game.
 */
class RoomFragment : Fragment() {

    private var _binding: RoomFragmentBinding? = null
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // list of users
    private lateinit var usernames: ArrayList<String>
    private val joinRoom: (String) -> Unit = { roomId ->
        Log.v("userName", activityViewModel.username + " is in " + roomId)

        val roomRef = database.getReference(activityViewModel.roomPath)
        if (activityViewModel.isHost) {
            roomRef.child("players").child(roomId).removeValue()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val username = activityViewModel.username
        val isHost = activityViewModel.isHost

        usernames = ArrayList()

        _binding = RoomFragmentBinding.inflate(inflater, container, false)
        binding.textViewRoomHeader.text =
            getString(R.string.welcome_username, activityViewModel.username)

        with(binding.listOfPlayers) {
            layoutManager = LinearLayoutManager(context)
            adapter =
                MyUsernameRecyclerViewAdapter(usernames, username, isHost, joinRoom, getString(R.string.kick))

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

// Decides the text of the prepare/start button with regard to the user type.
        if (activityViewModel.isHost)
            binding.textViewPrepareStart.setText(R.string.prepare_to_start_game)
        else
            binding.textViewPrepareStart.setText(R.string.start_game)

// Make the chat log scrollable when overflows
        binding.includeChatLogFragment.textViewChatLog.movementMethod =
            ScrollingMovementMethod()
        binding.includeChatLogFragment.buttonSendMessage.setOnClickListener(
            MySendMessageClickListener(
                requireContext(),
                binding.includeChatLogFragment.editTextChatMessage
            ) {
                if (binding.includeChatLogFragment.editTextChatMessage.editableText.toString() != "") {
                    binding.includeChatLogFragment.textViewChatLog.append(activityViewModel.username + ": ")
                    binding.includeChatLogFragment.textViewChatLog.append(binding.includeChatLogFragment.editTextChatMessage.editableText)
                    binding.includeChatLogFragment.textViewChatLog.append("\n")
                    binding.includeChatLogFragment.editTextChatMessage.editableText.clear()


                }
            }
        )

        binding.textViewPrepareStart.setOnClickListener {
// TODO define client prepare and unprepare events' actions
            findNavController().navigate(RoomFragmentDirections.actionStartGame())
        }

        val roomPath = activityViewModel.roomPath + "/players/"
        val roomRef = database.reference.child(roomPath)

        val roomListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val adapter = binding.listOfPlayers.adapter as MyUsernameRecyclerViewAdapter

                for (roomSnapshot in dataSnapshot.children) {
                    val players = roomSnapshot.key.toString()
                    adapter.addUser(players)
                }
                println(dataSnapshot.childrenCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("loadPost:onCancelled", databaseError.toException())
            }
        }
        roomRef.addValueEventListener(roomListener)


        binding.textViewPrepareStart.setOnClickListener {
            // TODO define client prepare and unprepare events' actions
            findNavController().navigate(RoomFragmentDirections.actionStartGame())
            roomRef.removeEventListener(roomListener)
        }

        val playerRef = database.getReference(activityViewModel.roomPath + "/players/")
        playerRef.child(activityViewModel.username).setValue("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}