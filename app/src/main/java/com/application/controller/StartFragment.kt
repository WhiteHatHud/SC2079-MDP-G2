package com.application.controller.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.application.controller.R
import com.application.controller.databinding.StartFragmentBinding
import android.media.MediaPlayer

class StartFragment : Fragment() {
    private var _binding: StartFragmentBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StartFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to FirstFragment when Start button is clicked
        binding.btnStart.setOnClickListener {
            playButtonSound()
            findNavController().navigate(R.id.action_StartFragment_to_FirstFragment)
        }

        binding.btnSettings.setOnClickListener {
            playButtonSound()
            findNavController().navigate(R.id.action_StartFragment_to_SecondFragment)
        }

        binding.btnCredits.setOnClickListener {
            playButtonSound()
            findNavController().navigate(R.id.action_StartFragment_to_MazeFragment)
        }
    }

    private fun playButtonSound() {
        mediaPlayer?.release() // Release previous media player instance if exists
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.button)
        mediaPlayer?.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
