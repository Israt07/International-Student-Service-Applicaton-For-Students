package com.company.iss.view.profileActivity

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.FragmentEditProfileBinding
import com.company.iss.model.UserModel
import com.company.iss.repository.EditProfileRepository
import com.company.iss.utils.LoadingDialog
import com.company.iss.utils.NetworkManager
import com.company.iss.utils.SharedPref
import com.company.iss.utils.loadImage
import com.company.iss.utils.showErrorToast
import com.company.iss.utils.showInfoToast
import com.company.iss.utils.showSuccessToast
import com.company.iss.utils.showWarningToast
import com.company.iss.view_model.EditProfileViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth

class EditProfileFragment : Fragment() {

    //declaring variables
    private lateinit var binding: FragmentEditProfileBinding

    private lateinit var repository: EditProfileRepository
    private lateinit var viewModel: EditProfileViewModel

    private lateinit var user: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())
        LoadingDialog.init(requireContext())

        repository = EditProfileRepository()
        viewModel = ViewModelProvider(this, EditProfileViewModelFactory(repository))[EditProfileViewModel::class.java]

        //request for data
        viewModel.requestUserDetails(FirebaseAuth.getInstance().currentUser!!.uid)

        observerList()

        //hiding update button
        binding.updateButton.visibility = View.GONE

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //profile pic click event
        binding.profilePicImageview.setOnClickListener {
            ImagePicker.with(this)
                .crop(5f, 5f)
                .compress(800)         //Final image size will be less than 0.8 MB(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        //update button click event
        binding.updateButton.setOnClickListener { updateProfile() }

        return binding.root
    }

    private fun observerList() {
        viewModel.userDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                user = it
                if (it.profile_pic_url != null) {
                    binding.profilePicImageview.loadImage(it.profile_pic_url)
                    SharedPref.write("PROFILE_PIC_URL", it.profile_pic_url!!)
                } else {
                    binding.profilePicImageview.loadImage(R.drawable.user_profile_pic_placeholder_image)
                }
                binding.nameEdittext.setText(it.name)
                SharedPref.write("USER_NAME", it.name.toString())
                binding.mobileNumberEdittext.setText(it.mobile_number)
                binding.updateButton.visibility = View.VISIBLE
            }
        }

        viewModel.toastMessageLiveData.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    "Updated" -> {
                        requireContext().showSuccessToast(it)
                    }
                    else -> requireContext().showErrorToast(it)
                }

                viewModel.resetToastMessage()
            }

            LoadingDialog.dismiss()
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    binding.profilePicImageview.loadImage(fileUri)
                    user.profile_pic_url = fileUri.toString()
                }
                ImagePicker.RESULT_ERROR -> {
                    requireContext().showErrorToast(ImagePicker.getError(data))
                }
                else -> {
                    requireContext().showInfoToast("Cancelled")
                }
            }
        }

    private fun updateProfile() {
        if (binding.nameEdittext.text.toString().trim().isEmpty()) {
            binding.nameEdittext.error = "Enter your name"
            binding.nameEdittext.requestFocus()
            return
        }
        if (binding.nameEdittext.text.toString().trim().contains("0") or binding.nameEdittext.text.toString().trim().contains("1") or binding.nameEdittext.text.toString().trim().contains("2") or binding.nameEdittext.text.toString().trim().contains("3") or binding.nameEdittext.text.toString().trim().contains("4") or binding.nameEdittext.text.toString().trim().contains("5") or binding.nameEdittext.text.toString().trim().contains("6") or binding.nameEdittext.text.toString().trim().contains("7") or binding.nameEdittext.text.toString().trim().contains("8") or binding.nameEdittext.text.toString().trim().contains("9")) {
            binding.nameEdittext.error = "Number not allowed"
            binding.nameEdittext.requestFocus()
            return
        }
        if (binding.mobileNumberEdittext.text.toString().trim().isEmpty()) {
            binding.mobileNumberEdittext.error = "Enter mobile number"
            binding.mobileNumberEdittext.requestFocus()
            return
        }

        user.name = binding.nameEdittext.text.toString().trim()
        user.mobile_number = binding.mobileNumberEdittext.text.toString().trim()

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Updating profile...")
            LoadingDialog.show()

            viewModel.updateProfile(user)
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }
}



class EditProfileViewModelFactory(private val repository: EditProfileRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = EditProfileViewModel(repository) as T
}