package com.company.iss.view.servicesActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.FragmentServicesBinding
import com.company.iss.utils.showErrorToast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ServicesFragment : Fragment() {

    //Declaring Variables
    private lateinit var binding: FragmentServicesBinding

    private var transportationLink = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentServicesBinding.inflate(inflater, container, false)

        //request for data
        getDataFromFirebase()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //housing button click event
        binding.housingButton.setOnClickListener { findNavController().navigate(R.id.action_servicesFragment_to_housingFragment) }

        //transportation button click event
        binding.transportationButton.setOnClickListener { openTransportation() }

        //health care button click event
        binding.healthCareButton.setOnClickListener { findNavController().navigate(R.id.action_servicesFragment_to_healthCareFragment) }

        //translator button click event
        binding.translatorButton.setOnClickListener { findNavController().navigate(R.id.action_servicesFragment_to_translatorFragment) }

        return binding.root
    }

    private fun getDataFromFirebase() {
        Firebase.database.reference.child("services").child("transportation").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    transportationLink = snapshot.child("transportationImageLink").value as String
                }
                binding.progressbar.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressbar.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }

        })
    }

    private fun openTransportation() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(transportationLink))
            startActivity(intent)
        } catch (e: Exception) {
            requireContext().showErrorToast("Link not valid")
        }
    }
}