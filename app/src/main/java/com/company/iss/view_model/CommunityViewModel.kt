package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.CommunityModel
import com.company.iss.repository.CommunityRepository

class CommunityViewModel(private val repository: CommunityRepository) : ViewModel() {
    private val _communityListLiveData = MutableLiveData<ArrayList<CommunityModel>?>()
    val communityListLiveData: LiveData<ArrayList<CommunityModel>?>
        get() = _communityListLiveData

    fun requestCommunityList() {
        repository.getCommunityList(_communityListLiveData)
    }
}