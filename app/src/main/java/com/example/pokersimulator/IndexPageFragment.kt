package com.example.pokersimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pokersimulator.databinding.FragmentIndexPageBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class IndexPageFragment : Fragment() {

    private var _binding: FragmentIndexPageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentIndexPageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCreateRoom.setOnClickListener {
            findNavController().navigate(IndexPageFragmentDirections.actionCreateRoom(true))
        }
//        TODO binding.buttonJoinRoom

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}