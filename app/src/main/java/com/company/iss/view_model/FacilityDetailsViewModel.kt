package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.FacilityImageModel
import com.company.iss.model.FacilityModel
import com.company.iss.repository.FacilityDetailsRepository

class FacilityDetailsViewModel(private val repository: FacilityDetailsRepository) : ViewModel() {
    private val _facilityDetailsLiveData = MutableLiveData<FacilityModel?>()
    val facilityDetailsLiveData: LiveData<FacilityModel?>
        get() = _facilityDetailsLiveData

    fun requestFacilityDetails(title: String) {
        repository.getFacilityDetails(title, _facilityDetailsLiveData)
    }

    private val _facilityImagesLiveData = MutableLiveData<ArrayList<FacilityImageModel>?>()
    val facilityImagesLiveData: LiveData<ArrayList<FacilityImageModel>?>
        get() = _facilityImagesLiveData

    fun requestFacilityImages(title: String) {
        repository.getFacilityImages(title, _facilityImagesLiveData)
    }
}