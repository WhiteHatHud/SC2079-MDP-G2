package com.application.controller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.application.controller.databinding.FragmentFirstBinding
import com.application.controller.MenuActivity
import com.application.controller.bluetooth.BluetoothService

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var btService  = MenuActivity.getBluetoothService()
        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonUp.setOnClickListener {
            //Send Forward command via bluetooth
            if (btService != null) {
                btService.sendOutMessage("FW10")
            }else
            {
                Toast.makeText(this.context, "No Bluetooth Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonDown.setOnClickListener {
            //Send Backward command via bluetooth
            if (btService != null) {
                btService.sendOutMessage("BW10")
            }else
            {
                Toast.makeText(this.context, "No Bluetooth Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonLeft.setOnClickListener {
            //Send Left command via bluetooth
            if (btService != null) {
                btService.sendOutMessage("FL00")
            }else
            {
                Toast.makeText(this.context, "No Bluetooth Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonRight.setOnClickListener {
            //Send Right command via bluetooth
            if (btService != null) {
                btService.sendOutMessage("FR00")
            }else
            {
                Toast.makeText(this.context, "No Bluetooth Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonBackLeft.setOnClickListener {
            if (btService != null) {
                btService.sendOutMessage("BL00")
            }else
            {
                Toast.makeText(this.context, "No Bluetooth Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonBackRight.setOnClickListener {
            if (btService != null) {
                btService.sendOutMessage("BR00")
            }else
            {
                Toast.makeText(this.context, "No Bluetooth Connection", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}