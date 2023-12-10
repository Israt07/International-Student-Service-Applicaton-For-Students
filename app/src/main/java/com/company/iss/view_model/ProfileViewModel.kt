package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.UserModel
import com.company.iss.repository.ProfileRepository

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val _userDetailsLiveData = MutableLiveData<UserModel?>()
    val userDetailsLiveData: LiveData<UserModel?>
        get() = _userDetailsLiveData

    fun requestUserDetails(userId: String) {
        repository.getUserDetails(userId, _userDetailsLiveData)
    }
}