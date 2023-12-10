package com.company.iss.view.appointmentActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.FragmentAppointmentBinding

class AppointmentFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentAppointmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAppointmentBinding.inflate(inflater, container, false)

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //make appointment button click event
        binding.makeAppointmentButton.setOnClickListener { findNavController().navigate(R.id.action_appointmentFragment_to_makeAppointmentFragment) }

        //cancel appointment button click event
        binding.cancelAppointmentButton.setOnClickListener { findNavController().navigate(R.id.action_appointmentFragment_to_cancelAppointmentFragment) }

        //view appointment button click event
        binding.viewAppointmentsButton.setOnClickListener { findNavController().navigate(R.id.action_appointmentFragment_to_viewAppointmentsFragment) }

        return binding.root
    }
}