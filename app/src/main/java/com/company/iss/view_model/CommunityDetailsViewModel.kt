package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.CommunityImageModel
import com.company.iss.model.CommunityModel
import com.company.iss.repository.CommunityDetailsRepository

class CommunityDetailsViewModel(private val repository: CommunityDetailsRepository) : ViewModel() {
    private val _communityDetailsLiveData = MutableLiveData<CommunityModel?>()
    val communityDetailsLiveData: LiveData<CommunityModel?>
        get() = _communityDetailsLiveData

    fun requestCommunityDetails(title: String) {
        repository.getCommunityDetails(title, _communityDetailsLiveData)
    }

    private val _communityImagesLiveData = MutableLiveData<ArrayList<CommunityImageModel>?>()
    val communityImagesLiveData: LiveData<ArrayList<CommunityImageModel>?>
        get() = _communityImagesLiveData

    fun requestCommunityImages(title: String) {
        repository.getCommunityImages(title, _communityImagesLiveData)
    }
}