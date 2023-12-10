package com.company.iss.view.facilityActivity.communityActivity

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
import com.company.iss.adapter.CommunityAdapter
import com.company.iss.databinding.FragmentCommunityBinding
import com.company.iss.interfaces.CommunityItemClickListener
import com.company.iss.model.CommunityModel
import com.company.iss.repository.CommunityRepository
import com.company.iss.view_model.CommunityViewModel

class CommunityFragment : Fragment(), CommunityItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentCommunityBinding

    private lateinit var repository: CommunityRepository
    private lateinit var viewModel: CommunityViewModel

    private var communityList = ArrayList<CommunityModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(inflater, container, false)

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        repository = CommunityRepository()
        viewModel = ViewModelProvider(this, CommunityViewModelFactory(repository))[CommunityViewModel::class.java]

        //request for data
        viewModel.requestCommunityList()

        observerList()

        //set recyclerview adapter
        binding.communityRecyclerview.adapter = CommunityAdapter(communityList, this)

        return binding.root
    }

    private fun observerList() {
        viewModel.communityListLiveData.observe(viewLifecycleOwner) {
            communityList.clear()
            if (it != null) {
                communityList.addAll(it)

                binding.communityRecyclerview.adapter?.notifyDataSetChanged()

                binding.noCommunityAvailableTextview.visibility = View.GONE
                binding.communityRecyclerview.visibility = View.VISIBLE
            } else {
                binding.communityRecyclerview.visibility = View.GONE
                binding.noCommunityAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onClick(currentItem: CommunityModel) {
        val bundle = bundleOf(
            "TITLE" to currentItem.title
        )
        findNavController().navigate(R.id.action_communityFragment_to_communityDetailsFragment, bundle)
    }
}



class CommunityViewModelFactory(private val repository: CommunityRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CommunityViewModel(repository) as T
}