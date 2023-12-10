package com.company.iss.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.adapter.HomeMenuAdapter
import com.company.iss.databinding.FragmentHomeBinding
import com.company.iss.interfaces.HomeMenuItemClickListener
import com.company.iss.model.HomeMenuModel
import com.company.iss.repository.HomeRepository
import com.company.iss.utils.SharedPref
import com.company.iss.utils.loadImage
import com.company.iss.view_model.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(), HomeMenuItemClickListener {
    //Declaring variables
    private lateinit var binding: FragmentHomeBinding

    private lateinit var repository: HomeRepository
    private lateinit var viewModel: HomeViewModel

    private lateinit var homeMenuList: ArrayList<HomeMenuModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())

        //set details
        updateView()

        repository = HomeRepository()
        viewModel = ViewModelProvider(this, HomeViewModelFactory(repository))[HomeViewModel::class.java]

        //request for data
        viewModel.requestUserDetails(FirebaseAuth.getInstance().currentUser!!.uid)

        observerList()

        homeMenuList = ArrayList()

        homeMenuList.add(HomeMenuModel(R.drawable.menu_profile_icon, "Profile"))
        homeMenuList.add(HomeMenuModel(R.drawable.menu_map_icon, "Map"))
        homeMenuList.add(HomeMenuModel(R.drawable.menu_service_icon, "Services"))
        homeMenuList.add(HomeMenuModel(R.drawable.menu_facilities_icon, "Facilities"))
        homeMenuList.add(HomeMenuModel(R.drawable.menu_appointment_icon, "Appointment"))
        homeMenuList.add(HomeMenuModel(R.drawable.menu_faq_icon, "FAQ"))
        homeMenuList.add(HomeMenuModel(R.drawable.menu_notification_icon, "Notification"))
        homeMenuList.add(HomeMenuModel(R.drawable.menu_payment_icon, "Payment"))
        homeMenuList.add(HomeMenuModel(R.drawable.menu_rating_icon, "Rating"))

        //set recyclerview adapter
        binding.homeMenuRecyclerview.adapter = HomeMenuAdapter(homeMenuList, this)

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
                SharedPref.write("USER_ID", it.user_id.toString())
                SharedPref.write("USER_NAME", it.name.toString())
                SharedPref.write("USER_GENDER", it.gender.toString())
                SharedPref.write("USER_EMAIL", it.email.toString())
                SharedPref.write("USER_MATRIC_NUMBER", it.matric_number.toString())
                SharedPref.write("USER_MOBILE_NUMBER", it.mobile_number.toString())
                SharedPref.write("USER_FACULTY", it.faculty.toString())
                SharedPref.write("USER_COURSE", it.course.toString())
                SharedPref.write("USER_COUNTRY", it.country.toString())
                SharedPref.write("USER_PASSPORT_NUMBER", it.passport_number.toString())
            }
        }
    }

    private fun updateView() {
        if (SharedPref.read("PROFILE_PIC_URL", "") == "") {
            binding.profilePicImageview.loadImage(R.drawable.user_profile_pic_placeholder_image)
        } else {
            binding.profilePicImageview.loadImage(SharedPref.read("PROFILE_PIC_URL", ""))
        }
        binding.userNameTextview.text = SharedPref.read("USER_NAME", "")
    }

    override fun onItemClick(currentItem: HomeMenuModel) {
        when(currentItem.menu_title) {
            "Profile" -> findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            "Map" -> findNavController().navigate(R.id.action_homeFragment_to_mapFragment)
            "Services" -> findNavController().navigate(R.id.action_homeFragment_to_servicesFragment)
            "Facilities" -> findNavController().navigate(R.id.action_homeFragment_to_facilitiesFragment)
            "Appointment" -> findNavController().navigate(R.id.action_homeFragment_to_appointmentFragment)
            "FAQ" -> findNavController().navigate(R.id.action_homeFragment_to_faqFragment)
            "Notification" -> findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
            "Payment" -> findNavController().navigate(R.id.action_homeFragment_to_paymentFragment)
            "Rating" -> findNavController().navigate(R.id.action_homeFragment_to_reviewRatingFragment)
        }
    }
}



class HomeViewModelFactory(private val repository: HomeRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(repository) as T
}