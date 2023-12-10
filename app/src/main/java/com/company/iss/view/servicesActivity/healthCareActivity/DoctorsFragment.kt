package com.company.iss.view.servicesActivity.healthCareActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.adapter.DoctorAdapter
import com.company.iss.databinding.FragmentDoctorsBinding
import com.company.iss.interfaces.DoctorItemClickListener
import com.company.iss.model.UserModel
import com.company.iss.repository.DoctorsRepository
import com.company.iss.view_model.DoctorsViewModel

class DoctorsFragment : Fragment(), DoctorItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentDoctorsBinding

    private lateinit var repository: DoctorsRepository
    private lateinit var viewModel: DoctorsViewModel

    private var doctorList = ArrayList<UserModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoctorsBinding.inflate(inflater, container, false)

        repository = DoctorsRepository()
        viewModel = ViewModelProvider(this, DoctorsViewModelFactory(repository))[DoctorsViewModel::class.java]

        //request for data
        viewModel.requestDoctorList()

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.doctorsRecyclerview.adapter = DoctorAdapter(doctorList,  this)

        return binding.root
    }
    private fun observerList() {
        viewModel.doctorListLiveData.observe(viewLifecycleOwner) {
            doctorList.clear()
            if (it != null) {
                doctorList.addAll(it)

                binding.doctorsRecyclerview.adapter?.notifyDataSetChanged()

                binding.noDoctorAvailableTextview.visibility = View.GONE
                binding.doctorsRecyclerview.visibility = View.VISIBLE
            } else {
                binding.doctorsRecyclerview.visibility = View.GONE
                binding.noDoctorAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onItemClick(currentItem: UserModel) {
        val bundle = bundleOf(
            "USER_ID" to currentItem.user_id
        )
        findNavController().navigate(R.id.action_doctorsFragment_to_doctorProfileFragment, bundle)
    }
}



class DoctorsViewModelFactory(private val repository: DoctorsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = DoctorsViewModel(repository) as T
}