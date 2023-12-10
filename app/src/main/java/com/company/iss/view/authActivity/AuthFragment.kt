package com.company.iss.view.authActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAuthBinding.inflate(inflater, container, false)

        //login button click
        binding.loginButton.setOnClickListener { findNavController().navigate(R.id.action_authFragment_to_loginFragment) }

        //register button click
        binding.registerButton.setOnClickListener { findNavController().navigate(R.id.action_authFragment_to_registerFragment) }

        return binding.root
    }
}