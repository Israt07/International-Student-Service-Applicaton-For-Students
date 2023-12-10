package com.company.iss.view.servicesActivity.healthCareActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.FragmentHealthCareBinding

class HealthCareFragment : Fragment() {

    //Declaring Variables
    private lateinit var binding: FragmentHealthCareBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHealthCareBinding.inflate(inflater, container, false)

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        binding.doctorsButton.setOnClickListener { findNavController().navigate(R.id.action_healthCareFragment_to_doctorsFragment) }

        binding.reportsButton.setOnClickListener { findNavController().navigate(R.id.action_healthCareFragment_to_reportsFragment) }

        return binding.root
    }
}