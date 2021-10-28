package com.example.pokersimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pokersimulator.databinding.RoomFragmentBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class RoomFragment : Fragment() {

    private var _binding: RoomFragmentBinding? = null
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RoomFragmentBinding.inflate(inflater, container, false)
        binding.textviewRoomHeader.text = getString(R.string.welcome_username, activityViewModel.username)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Decides the text of the prepare/start button with regard to the user type.
        if (activityViewModel.isHost)
            binding.textViewPrepareStart.setText(R.string.prepare_to_start_game)
        else
            binding.textViewPrepareStart.setText(R.string.start_game)

        binding.buttonPrepareStartLayout.setOnClickListener {
            // TODO define client prepare and unprepare events' actions
            findNavController().navigate(RoomFragmentDirections.actionStartGame())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}