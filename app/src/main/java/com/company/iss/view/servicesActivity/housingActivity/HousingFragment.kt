package com.company.iss.view.servicesActivity.housingActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.FragmentHousingBinding

class HousingFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentHousingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHousingBinding.inflate(inflater, container, false)

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //inside campus button click event
        binding.insideCampusButton.setOnClickListener { goToHousingDetailsPage("Inside Campus") }

        //view booking history button click event
        binding.viewBookingHistoryButton.setOnClickListener { findNavController().navigate(R.id.action_housingFragment_to_viewBookingHistoryFragment) }

        return binding.root
    }

    private fun goToHousingDetailsPage(title: String?) {
        val bundle = bundleOf(
            "TITLE" to title
        )
        findNavController().navigate(R.id.action_housingFragment_to_housingDetailsFragment, bundle)
    }
}