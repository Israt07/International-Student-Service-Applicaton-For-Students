package com.company.iss.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.adapter.NotificationAdapter
import com.company.iss.databinding.FragmentNotificationBinding
import com.company.iss.model.NotificationModel
import com.company.iss.repository.NotificationRepository
import com.company.iss.view_model.NotificationViewModel

class NotificationFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentNotificationBinding

    private lateinit var repository: NotificationRepository
    private lateinit var viewModel: NotificationViewModel

    private var notificationList = ArrayList<NotificationModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater, container, false)

        repository = NotificationRepository()
        viewModel = ViewModelProvider(this, NotificationViewModelFactory(repository))[NotificationViewModel::class.java]

        //request for data
        viewModel.requestNotificationList()

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.notificationRecyclerview.adapter = NotificationAdapter(notificationList)

        return binding.root
    }

    private fun observerList() {
        viewModel.notificationListLiveData.observe(viewLifecycleOwner) {
            notificationList.clear()
            if (it != null) {
                notificationList.addAll(it)
                notificationList.reverse()

                binding.notificationRecyclerview.adapter?.notifyDataSetChanged()

                binding.noNotificationAvailableTextview.visibility = View.GONE
                binding.notificationRecyclerview.visibility = View.VISIBLE
            } else {
                binding.notificationRecyclerview.visibility = View.GONE
                binding.noNotificationAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }
}



class NotificationViewModelFactory(private val repository: NotificationRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = NotificationViewModel(repository) as T
}