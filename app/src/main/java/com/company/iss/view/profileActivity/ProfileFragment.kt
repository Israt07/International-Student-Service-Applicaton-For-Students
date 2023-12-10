package com.company.iss.view.profileActivity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.FragmentProfileBinding
import com.company.iss.repository.ProfileRepository
import com.company.iss.utils.SharedPref
import com.company.iss.utils.loadImage
import com.company.iss.utils.showSuccessToast
import com.company.iss.view_model.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    //declaring variables
    private lateinit var binding: FragmentProfileBinding

    private lateinit var repository: ProfileRepository
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())

        repository = ProfileRepository()
        viewModel = ViewModelProvider(this, ProfileViewModelFactory(repository))[ProfileViewModel::class.java]

        //request for data
        viewModel.requestUserDetails(FirebaseAuth.getInstance().currentUser!!.uid)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

            //edit profile icon click event
        binding.editProfileIcon.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment) }

        //logout button click event
        binding.logoutButton.setOnClickListener { logout() }

        return binding.root
    }

    private fun observerList() {
        viewModel.userDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.profile_pic_url != null) {
                    binding.profilePicImageview.loadImage(it.profile_pic_url)
                    SharedPref.write("PROFILE_PIC_URL", it.profile_pic_url!!)
                } else {
                    binding.profilePicImageview.loadImage(R.drawable.user_profile_pic_placeholder_image)
                }

                //set details
                binding.userNameTextview.text = it.name
                SharedPref.write("USER_NAME", it.name.toString())
                binding.emailTextview.text = it.email
                binding.genderTextview.text = it.gender
                binding.matricNumberTextview.text = it.matric_number
                binding.mobileNumberTextview.text = it.mobile_number
                binding.facultyTextview.text = it.faculty
                binding.courseTextview.text = it.course
                binding.dateOfBirthTextview.text = it.date_of_birth
                binding.countryTextview.text = it.country
                binding.passportNumberTextview.text = it.passport_number
            }
        }
    }

    private fun logout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage("Do you really want to logout")
        builder.setPositiveButton("Yes") { _, _ ->
            FirebaseAuth.getInstance().signOut()
            requireContext().showSuccessToast("Logged out")
            findNavController().navigate(R.id.action_profileFragment_to_splashFragment)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}



class ProfileViewModelFactory(private val repository: ProfileRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ProfileViewModel(repository) as T
}