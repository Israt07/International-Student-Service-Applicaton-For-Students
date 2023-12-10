package com.company.iss.view.servicesActivity.healthCareActivity.reportsActivity

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
import com.company.iss.adapter.ReportsAdapter
import com.company.iss.databinding.FragmentReportsBinding
import com.company.iss.interfaces.ReportsItemClickListener
import com.company.iss.model.DoctorReportModel
import com.company.iss.repository.ReportsRepository
import com.company.iss.view_model.ReportsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

class ReportsFragment : Fragment(), ReportsItemClickListener {

    private lateinit var binding: FragmentReportsBinding

    private lateinit var repository: ReportsRepository
    private lateinit var viewModel: ReportsViewModel

    private var reportsList = ArrayList<DoctorReportModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportsBinding.inflate(inflater, container, false)

        repository = ReportsRepository()
        viewModel = ViewModelProvider(this, ReportsViewModelFactory(repository))[ReportsViewModel::class.java]

        //request for data
        viewModel.requestReportsList(FirebaseAuth.getInstance().currentUser!!.uid)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.reportsRecyclerview.adapter = ReportsAdapter(reportsList,  this)

        return binding.root
    }

    private fun observerList() {
        viewModel.reportListLiveData.observe(viewLifecycleOwner) {
            reportsList.clear()
            if (it != null) {
                reportsList.addAll(it)

                binding.reportsRecyclerview.adapter?.notifyDataSetChanged()

                binding.nothingAvailableTextview.visibility = View.GONE
                binding.reportsRecyclerview.visibility = View.VISIBLE
            } else {
                binding.reportsRecyclerview.visibility = View.GONE
                binding.nothingAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onItemClick(currentItem: DoctorReportModel) {
        val bundle = bundleOf(
            "DATA" to Gson().toJson(currentItem)
        )
        findNavController().navigate(R.id.action_reportsFragment_to_reportsDetailsFragment, bundle)
    }
}




class ReportsViewModelFactory(private val repository: ReportsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ReportsViewModel(repository) as T
}