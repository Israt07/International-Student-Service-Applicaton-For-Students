package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.HousingImageModel
import com.company.iss.model.HousingModel
import com.company.iss.repository.HousingDetailsRepository

class HousingDetailsViewModel(private val repository: HousingDetailsRepository) : ViewModel() {
    private val _housingDetailsLiveData = MutableLiveData<HousingModel?>()
    val housingDetailsLiveData: LiveData<HousingModel?>
        get() = _housingDetailsLiveData

    fun requestHousingDetails(title: String) {
        repository.getHousingDetails(title, _housingDetailsLiveData)
    }

    private val _housingImagesLiveData = MutableLiveData<ArrayList<HousingImageModel>?>()
    val housingImagesLiveData: LiveData<ArrayList<HousingImageModel>?>
        get() = _housingImagesLiveData

    fun requestHousingImages(title: String) {
        repository.getHousingImages(title, _housingImagesLiveData)
    }
}