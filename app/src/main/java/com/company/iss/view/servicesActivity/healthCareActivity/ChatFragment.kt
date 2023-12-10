package com.company.iss.view.servicesActivity.healthCareActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.adapter.ChatAdapter
import com.company.iss.databinding.FragmentChatBinding
import com.company.iss.model.ChatModel
import com.company.iss.repository.ChatRepository
import com.company.iss.utils.NetworkManager
import com.company.iss.utils.SharedPref
import com.company.iss.utils.showErrorToast
import com.company.iss.utils.showWarningToast
import com.company.iss.view_model.ChatViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentChatBinding

    private lateinit var repository: ChatRepository
    private lateinit var viewModel: ChatViewModel

    private var doctorId = ""
    private var doctorName = ""

    private var chatList = ArrayList<ChatModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())

        doctorId = arguments?.getString("USER_ID").toString()
        doctorName = arguments?.getString("USER_NAME").toString()

        //set title text
        binding.titleTextview.text = doctorName

        repository = ChatRepository()
        viewModel = ViewModelProvider(this, ChatViewModelFactory(repository))[ChatViewModel::class.java]

        //request for data
        viewModel.requestChatList(SharedPref.read("USER_ID", "").toString())

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.chatListRecyclerview.adapter = ChatAdapter(chatList)

        //send button click event
        binding.sendButton.setOnClickListener { sendMessage() }

        return binding.root
    }

    private fun observerList() {
        viewModel.chatListLiveData.observe(viewLifecycleOwner) {
            chatList.clear()
            if (it != null) {
                chatList.addAll(it.filter { item -> item.doctor_id == doctorId })

                binding.chatListRecyclerview.adapter?.notifyDataSetChanged()

                binding.chatListRecyclerview.visibility = View.VISIBLE

                //scroll to end of the screen
                binding.chatListRecyclerview.scrollToPosition(binding.chatListRecyclerview.adapter?.itemCount!! - 1)
            } else {
                binding.chatListRecyclerview.visibility = View.GONE
            }
            binding.progressbar.visibility = View.GONE
            binding.mainLayout.visibility = View.VISIBLE


        }
    }

    private fun sendMessage() {
        if (binding.messageEdittext.text.toString().trim().isEmpty()) {
            requireContext().showWarningToast("Enter message")
            return
        }

        if (NetworkManager.isInternetAvailable(requireContext())) {

            val chatId = Firebase.database.reference.child("chats").push().key.toString()

            val chatModel = ChatModel(chatId, SharedPref.read("USER_ID", "").toString(), doctorId, SharedPref.read("USER_NAME", "").toString(), doctorName, binding.messageEdittext.text.toString().trim(), SharedPref.read("USER_ID", "").toString())

            Firebase.database.reference.child("chats").child(chatId)
                .setValue(chatModel).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.messageEdittext.setText("")
                    } else {
                        requireContext().showErrorToast("Something wrong.")
                    }
                }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }
}




class ChatViewModelFactory(private val repository: ChatRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ChatViewModel(repository) as T
}