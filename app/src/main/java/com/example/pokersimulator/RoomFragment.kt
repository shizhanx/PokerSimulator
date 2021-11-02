package com.example.pokersimulator

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokersimulator.common.MyUserRecyclerViewAdapter
import com.example.pokersimulator.databinding.RoomFragmentBinding
import com.example.pokersimulator.listener.MySendMessageClickListener

/**
 * A fragment for users joined the same host to see each other and prepare for the game.
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
        binding.textViewRoomHeader.text = getString(R.string.welcome_username, activityViewModel.username)
        with(binding.listOfPlayers) {
            layoutManager = LinearLayoutManager(context)
            adapter = MyUserRecyclerViewAdapter()
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
        binding.includeChatLogFragment.textViewChatLog.movementMethod = ScrollingMovementMethod()
        binding.includeChatLogFragment.buttonSendMessage.setOnClickListener(
            MySendMessageClickListener(requireContext(), binding.includeChatLogFragment.editTextChatMessage) {
                if (binding.includeChatLogFragment.editTextChatMessage.editableText.toString() != "") {
                    //TODO Network part: send messages online
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}