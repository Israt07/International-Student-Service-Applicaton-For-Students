package com.company.iss.view.faqActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.adapter.FaqQuestionAnswerAdapter
import com.company.iss.databinding.FragmentFaqQuestionAnswerBinding
import com.company.iss.databinding.SingleFaqQuestionAnswerItemBinding
import com.company.iss.interfaces.FaqQuestionAnswerItemClickListener
import com.company.iss.model.FaqModel
import com.company.iss.repository.FaqQuestionAnswerRepository
import com.company.iss.utils.loadImage
import com.company.iss.view_model.FaqQuestionAnswerViewModel

class FaqQuestionAnswerFragment : Fragment(), FaqQuestionAnswerItemClickListener {

    //Declaring Variables
    private lateinit var binding: FragmentFaqQuestionAnswerBinding

    private lateinit var repository: FaqQuestionAnswerRepository
    private lateinit var viewModel: FaqQuestionAnswerViewModel

    private var title = ""
    private var faqList = ArrayList<FaqModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFaqQuestionAnswerBinding.inflate(inflater, container, false)

        title = arguments?.getString("TITLE").toString()

        repository = FaqQuestionAnswerRepository()
        viewModel = ViewModelProvider(this, FaqQuestionAnswerViewModelFactory(repository))[FaqQuestionAnswerViewModel::class.java]

        ////request for data
        viewModel.requestFaqList(title)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set title text
        binding.titleTextview.text = title

        //set recyclerview adapter
        binding.faqRecyclerview.adapter = FaqQuestionAnswerAdapter(faqList,  this)

        return binding.root
    }

    private fun observerList() {
        viewModel.faqListLiveData.observe(viewLifecycleOwner) {
            faqList.clear()
            if (it != null) {
                faqList.addAll(it)

                binding.faqRecyclerview.adapter?.notifyDataSetChanged()

                binding.noFaqAvailableTextview.visibility = View.GONE
                binding.faqRecyclerview.visibility = View.VISIBLE
            } else {
                binding.faqRecyclerview.visibility = View.GONE
                binding.noFaqAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onQuestionButtonClick(currentItem: FaqModel, adapterBinding: SingleFaqQuestionAnswerItemBinding) {
        if (adapterBinding.answerTextview.isVisible) {
            adapterBinding.answerTextview.visibility = View.GONE
            adapterBinding.indicationImageview.loadImage(R.drawable.expand_icon)
        } else {
            adapterBinding.answerTextview.visibility = View.VISIBLE
            adapterBinding.indicationImageview.loadImage(R.drawable.collapse_icon)
        }
    }
}



class FaqQuestionAnswerViewModelFactory(private val repository: FaqQuestionAnswerRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = FaqQuestionAnswerViewModel(repository) as T
}