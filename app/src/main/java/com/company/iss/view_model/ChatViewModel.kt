package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.ChatModel
import com.company.iss.repository.ChatRepository

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {
    private val _chatListLiveData = MutableLiveData<ArrayList<ChatModel>?>()
    val chatListLiveData: LiveData<ArrayList<ChatModel>?>
        get() = _chatListLiveData

    fun requestChatList(studentId: String) = repository.getChatList(studentId, _chatListLiveData)
}