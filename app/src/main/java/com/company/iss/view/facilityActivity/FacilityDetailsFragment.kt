package com.company.iss.view.facilityActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.adapter.FacilityImageAdapter
import com.company.iss.databinding.FragmentFacilityDetailsBinding
import com.company.iss.model.FacilityImageModel
import com.company.iss.repository.FacilityDetailsRepository
import com.company.iss.view_model.FacilityDetailsViewModel

class FacilityDetailsFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentFacilityDetailsBinding

    private lateinit var repository: FacilityDetailsRepository
    private lateinit var viewModel: FacilityDetailsViewModel

    private var title = ""
    private var facilityImageList = ArrayList<FacilityImageModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFacilityDetailsBinding.inflate(inflater, container, false)

        title = arguments?.getString("TITLE").toString()

        repository = FacilityDetailsRepository()
        viewModel = ViewModelProvider(this, FacilityDetailsViewModelFactory(repository))[FacilityDetailsViewModel::class.java]

        //request for data
        viewModel.requestFacilityImages(title)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set title text
        binding.titleTextview.text = title

        //set recyclerview adapter
        binding.imageRecyclerview.adapter = FacilityImageAdapter(facilityImageList)

        return binding.root
    }

    private fun observerList() {
        viewModel.facilityImagesLiveData.observe(viewLifecycleOwner) {
            facilityImageList.clear()
            if (it != null) {
                facilityImageList.addAll(it)

                facilityImageList.reverse()

                binding.imageRecyclerview.adapter?.notifyDataSetChanged()
            }
            //request for data
            viewModel.requestFacilityDetails(title)
        }

        viewModel.facilityDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.detailsTextview.text = it.details
            }
        }
    }
}



class FacilityDetailsViewModelFactory(private val repository: FacilityDetailsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = FacilityDetailsViewModel(repository) as T
}