package com.application.controller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.application.controller.R
import com.application.controller.databinding.StartFragmentBinding

class StartFragment : Fragment() {
    private var _binding: StartFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StartFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation to FirstFragment
        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_StartFragment_to_FirstFragment)
        }

        // Navigation to Settings
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_StartFragment_to_SecondFragment)
        }

        // Navigation to Credits (MazeFragment)
        binding.btnCredits.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_MazeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
