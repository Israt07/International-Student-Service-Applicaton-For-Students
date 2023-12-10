package com.company.iss.view.servicesActivity.healthCareActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.FragmentDoctorProfileBinding
import com.company.iss.repository.DoctorProfileRepository
import com.company.iss.view_model.DoctorProfileViewModel


class DoctorProfileFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentDoctorProfileBinding

    private lateinit var repository: DoctorProfileRepository
    private lateinit var viewModel: DoctorProfileViewModel

    private var userId = ""
    private var userName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoctorProfileBinding.inflate(inflater, container, false)

        userId = arguments?.getString("USER_ID").toString()

        repository = DoctorProfileRepository()
        viewModel = ViewModelProvider(this, DoctorProfileViewModelFactory(repository))[DoctorProfileViewModel::class.java]

        //request for data
        viewModel.requestUserDetails(userId)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //edit button click event
        binding.chatButton.setOnClickListener {
            val bundle = bundleOf(
                "USER_ID" to userId,
                "USER_NAME" to userName
            )
            findNavController().navigate(R.id.action_doctorProfileFragment_to_chatFragment, bundle)
        }

        return binding.root
    }

    private fun observerList() {
        viewModel.userDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                userName = it.name.toString()
                binding.nameTextview.text = it.name
                binding.emailTextview.text = it.email
                binding.mobileNumberTextview.text = it.mobile_number
                binding.idNumberTextview.text = it.matric_number
                binding.countryTextview.text = it.country
                binding.bioTextview.text = it.bio
                binding.specialistInTextview.text = it.specialist_in

                binding.progressbar.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
                binding.chatSection.visibility = View.VISIBLE
            }
        }
    }
}




class DoctorProfileViewModelFactory(private val repository: DoctorProfileRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = DoctorProfileViewModel(repository) as T
}