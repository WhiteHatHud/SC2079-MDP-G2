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
import com.application.controller.bluetooth.BluetoothSendData
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
                val newBtSendData= BluetoothSendData("control","FW010")
                btService.sendOutData(newBtSendData)
               // btService.sendOutMessage("FW010")

            }else
            {
                Toast.makeText(this.context, "No Bluetooth Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonDown.setOnClickListener {
            //Send Backward command via bluetooth
            if (btService != null) {
                val newBtSendData= BluetoothSendData("control","BW010")
                btService.sendOutData(newBtSendData)
                //btService.sendOutMessage("BW010")
            }else
            {
                Toast.makeText(this.context, "No Bluetooth Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonLeft.setOnClickListener {
            //Send Left command via bluetooth
            if (btService != null) {
                val newBtSendData= BluetoothSendData("control","FL000")
                btService.sendOutData(newBtSendData)
               // btService.sendOutMessage("FL000")
            }else
            {
                Toast.makeText(this.context, "No Bluetooth Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonRight.setOnClickListener {
            //Send Right command via bluetooth
            if (btService != null) {
                val newBtSendData= BluetoothSendData("control","FR000")
                btService.sendOutData(newBtSendData)
                //btService.sendOutMessage("FR000")
            }else
            {
                Toast.makeText(this.context, "No Bluetooth Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonBackLeft.setOnClickListener {
            if (btService != null) {
                val newBtSendData= BluetoothSendData("control","BL000")
                btService.sendOutData(newBtSendData)
               // btService.sendOutMessage("BL000")
            }else
            {
                Toast.makeText(this.context, "No Bluetooth Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonBackRight.setOnClickListener {
            if (btService != null) {
                val newBtSendData= BluetoothSendData("control","BR000")
                btService.sendOutData(newBtSendData)
                //btService.sendOutMessage("BR000")
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