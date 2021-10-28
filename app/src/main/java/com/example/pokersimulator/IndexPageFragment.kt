package com.example.pokersimulator

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pokersimulator.databinding.IndexPageFragmentBinding
import kotlin.random.Random

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class IndexPageFragment : Fragment() {

    private var _binding: IndexPageFragmentBinding? = null
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    // this is for closing the keyboard after user finishes input
    private val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = IndexPageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonUsernameSubmit.setOnClickListener {
            // Saves the username to the viewModel for other fragments to access
            val input = binding.usernameInputText.text.toString()
            if (input != "" && input != "null") {
                activityViewModel.username =
                    input + "#" + Random.nextInt(1000, 9999)
                binding.textViewUsernamePrompt.text = getString(R.string.welcome_username, activityViewModel.username)
                // Close keyboard and clear focus so that the user can see the buttons appearing below
                binding.usernameInputLayout.clearFocus()
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                binding.buttonCreateRoom.visibility = View.VISIBLE
                binding.buttonJoinRoom.visibility = View.VISIBLE
            }
        }
        binding.buttonCreateRoom.setOnClickListener {
            findNavController().navigate(IndexPageFragmentDirections.actionCreateRoom())
        }
        binding.buttonJoinRoom.setOnClickListener {
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}