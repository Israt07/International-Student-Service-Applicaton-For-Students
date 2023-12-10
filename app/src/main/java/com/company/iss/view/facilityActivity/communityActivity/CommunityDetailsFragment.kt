package com.company.iss.view.facilityActivity.communityActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.adapter.CommunityImageAdapter
import com.company.iss.databinding.FragmentCommunityDetailsBinding
import com.company.iss.model.CommunityImageModel
import com.company.iss.repository.CommunityDetailsRepository
import com.company.iss.view_model.CommunityDetailsViewModel

class CommunityDetailsFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentCommunityDetailsBinding

    private lateinit var repository: CommunityDetailsRepository
    private lateinit var viewModel: CommunityDetailsViewModel

    private var title = ""
    private var communityImageList = ArrayList<CommunityImageModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCommunityDetailsBinding.inflate(inflater, container, false)

        title = arguments?.getString("TITLE").toString()

        repository = CommunityDetailsRepository()
        viewModel = ViewModelProvider(this, CommunityDetailsViewModelFactory(repository))[CommunityDetailsViewModel::class.java]

        //request for data
        viewModel.requestCommunityImages(title)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set title text
        binding.titleTextview.text = title

        //set recyclerview adapter
        binding.imageRecyclerview.adapter = CommunityImageAdapter(communityImageList)

        return binding.root
    }

    private fun observerList() {
        viewModel.communityImagesLiveData.observe(viewLifecycleOwner) {
            communityImageList.clear()
            if (it != null) {
                communityImageList.addAll(it)

                communityImageList.reverse()

                binding.imageRecyclerview.adapter?.notifyDataSetChanged()
            }
            //request for data
            viewModel.requestCommunityDetails(title)
        }

        viewModel.communityDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.detailsTextview.text = it.details
            }
        }
    }
}



class CommunityDetailsViewModelFactory(private val repository: CommunityDetailsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CommunityDetailsViewModel(repository) as T
}