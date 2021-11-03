package com.example.pokersimulator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class IndexPageFragment : Fragment() {

    private var _binding: IndexPageFragmentBinding? = null
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    val database = Firebase.database("https://mystical-binder-330900-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val roomRef = database.getReference("rooms")



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
            roomRef.setValue("Testing room")
            val playerRef = database.getReference("rooms/Testing Room/players")
            playerRef.setValue(activityViewModel.username)
        }
        binding.buttonJoinRoom.setOnClickListener {
            activityViewModel.isHost = false
            findNavController().navigate(IndexPageFragmentDirections.actionJoinRoom())
            val playerRef = database.getReference("rooms/Testing Room/players")
            playerRef.child(activityViewModel.username).setValue("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}