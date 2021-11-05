package com.example.pokersimulator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokersimulator.MainActivity.Companion.database
import com.example.pokersimulator.common.MyUsernameRecyclerViewAdapter
import com.example.pokersimulator.listener.MySendMessageClickListener
import com.example.pokersimulator.databinding.ListRoomFragmentBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


/**
 * A fragment that shows a list of nearby hosts to the user when he selects join existing room
 * on the index page.
 */
class ListRoomFragment : Fragment() {

    private var _binding: ListRoomFragmentBinding? = null
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // list of lobby
    private lateinit var lobbynames : ArrayList<String>

    private val joinRoom: (String) -> Unit = { roomId ->
        Log.v("userName", activityViewModel.username + " is in " + roomId)

        activityViewModel.roomPath = "rooms/$roomId"
        val playerRef = database.getReference(activityViewModel.roomPath + "/players/")
        playerRef.child(activityViewModel.username).setValue("")
        findNavController().navigate(RoomFragmentDirections.actionStartGame())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val username = activityViewModel.username
        val isHost = activityViewModel.isHost
        val inRoom = false

        lobbynames = ArrayList()

        _binding = ListRoomFragmentBinding.inflate(inflater, container, false)
        binding.textViewListRoomHeader.text = username

        with(binding.listOfRooms) {
            layoutManager = LinearLayoutManager(context)

            adapter = MyUsernameRecyclerViewAdapter(lobbynames, username, isHost, inRoom, joinRoom, "Join")

        }

//        binding.listOfRooms.buttonAction.setOnClickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val roomRef = database.reference.child("rooms")

        val roomListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // TODO Get List of rooms and use the values to update the UI
                val adapter = binding.listOfRooms.adapter as MyUsernameRecyclerViewAdapter
                for (roomSnapshot in dataSnapshot.children) {
                    val roomsList = roomSnapshot.key.toString()
                    adapter.addUser(roomsList)
                    Log.w("Rooms ", roomsList)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("loadPost:onCancelled", databaseError.toException())
            }
        }
        roomRef.addValueEventListener(roomListener)

        // TODO define navigation to the room fragment
        //once accepted
        var roomJoined = "testing 8878"
        activityViewModel.roomPath = "rooms/" + "testing 8878"+ "/players"

        val playerRef = database.getReference(activityViewModel.roomPath)
        playerRef.child(activityViewModel.username).setValue("")
        findNavController().navigate(ListRoomFragmentDirections.actionJoinSelectedRoom())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}