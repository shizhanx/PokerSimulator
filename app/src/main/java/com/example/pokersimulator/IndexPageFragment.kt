package com.example.pokersimulator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pokersimulator.databinding.IndexPageFragmentBinding
import com.example.pokersimulator.listener.MySendMessageClickListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener
import kotlin.collections.MutableIterable as MutableIterable
import com.google.firebase.database.DatabaseReference

import android.R.string.no






/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class IndexPageFragment : Fragment() {

    private var _binding: IndexPageFragmentBinding? = null
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    val database = Firebase.database("https://mystical-binder-330900-default-rtdb.asia-southeast1.firebasedatabase.app/")




    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // The activity result registries for getting the permission and selecting an image locally
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        activityViewModel.imageURI.value = it
    }
    private var canSelectImage = true
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        canSelectImage = it
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = IndexPageFragmentBinding.inflate(inflater, container, false)
        // Initialize connections so that the user can see the latest connections for next round of the game
        activityViewModel.resetConnections()
        // Determine if the app is opened just now or the user pressed back button
        if (activityViewModel.username != "") {
            binding.textViewIndexHeader.text = getString(R.string.welcome_username, activityViewModel.username)
            binding.buttonCreateRoom.visibility = View.VISIBLE
            binding.buttonJoinRoom.visibility = View.VISIBLE
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonUsernameSubmit.setOnClickListener(
            MySendMessageClickListener(requireContext(), binding.textInputLayoutUsername) {
                // Saves the username to the viewModel for other fragments to access
                val input = binding.editTextUsername.text.toString()
                if (input != "" && input != "null") {
                    activityViewModel.username =
                        input + " " + Random.nextInt(1000, 9999)
                    binding.textViewIndexHeader.text = getString(R.string.welcome_username, activityViewModel.username)
                    binding.buttonCreateRoom.visibility = View.VISIBLE
                    binding.buttonJoinRoom.visibility = View.VISIBLE
                }
            }
        )

        activityViewModel.imageURI.observe(viewLifecycleOwner) {
            if (it != null) binding.imageViewSelectedImage.setImageURI(it)
        }

        binding.buttonSelectImage.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                canSelectImage = false
                requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (canSelectImage) getContent.launch("image/*")
        }

        // The isHost value should only be modified here, where the user chooses for the rest of the game
        binding.buttonCreateRoom.setOnClickListener {
            activityViewModel.isHost = true
            findNavController().navigate(IndexPageFragmentDirections.actionCreateRoom())

            activityViewModel.roomPath = "rooms/" + activityViewModel.username
            val playerRef = database.getReference(activityViewModel.roomPath + "/players/")
            playerRef.child(activityViewModel.username).setValue("")


        }
        binding.buttonJoinRoom.setOnClickListener {
            activityViewModel.isHost = false
            findNavController().navigate(IndexPageFragmentDirections.actionJoinRoom())
            val roomRef = database.reference.child("rooms")

            val roomListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // TODO Get List of rooms and use the values to update the UI
                    for (roomSnapshot in dataSnapshot.getChildren()) {
                        val roomsList = roomSnapshot.getKey().toString()
                        Log.w("Rooms: ", roomsList)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("loadPost:onCancelled", databaseError.toException())
                }
            }
            roomRef.addValueEventListener(roomListener)

            //TODO Change this from activityViewModel.username to the username of the host of the selected room
            activityViewModel.roomPath = "rooms/" + activityViewModel.username



            val playerRef = database.getReference(activityViewModel.roomPath + "/players/")
            playerRef.child(activityViewModel.username).setValue("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}