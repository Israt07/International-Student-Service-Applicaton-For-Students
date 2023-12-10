package com.company.iss.view.faqActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.adapter.HandbookAdapter
import com.company.iss.databinding.FragmentHandbookBinding
import com.company.iss.interfaces.HandbookItemClickListener
import com.company.iss.model.HandbookModel
import com.company.iss.repository.HandbookRepository
import com.company.iss.utils.SharedPref
import com.company.iss.utils.showErrorToast
import com.company.iss.view_model.HandbookViewModel

class HandbookFragment : Fragment(), HandbookItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentHandbookBinding

    private lateinit var repository: HandbookRepository
    private lateinit var viewModel: HandbookViewModel

    private var handbookList = ArrayList<HandbookModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHandbookBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())

        repository = HandbookRepository()
        viewModel = ViewModelProvider(this, HandbookViewModelFactory(repository))[HandbookViewModel::class.java]

        //request for data
        viewModel.requestHandbookList(SharedPref.read("USER_FACULTY", "").toString())

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.handbookRecyclerview.adapter = HandbookAdapter(handbookList,  this)

        return binding.root
    }

    private fun observerList() {
        viewModel.handbookListLiveData.observe(viewLifecycleOwner) {
            handbookList.clear()
            if (it != null) {
                handbookList.addAll(it)

                binding.handbookRecyclerview.adapter?.notifyDataSetChanged()

                binding.noHandbookAvailableTextview.visibility = View.GONE
                binding.handbookRecyclerview.visibility = View.VISIBLE
            } else {
                binding.handbookRecyclerview.visibility = View.GONE
                binding.noHandbookAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    private fun openLink(url: String?) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            requireContext().showErrorToast("Link not valid")
        }
    }

    override fun onHandbookButtonClick(currentItem: HandbookModel) {
        openLink(currentItem.file_link)
    }
}



class HandbookViewModelFactory(private val repository: HandbookRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HandbookViewModel(repository) as T
}