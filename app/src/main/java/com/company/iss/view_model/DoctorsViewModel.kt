package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.UserModel
import com.company.iss.repository.DoctorsRepository

class DoctorsViewModel(private val repository: DoctorsRepository) : ViewModel() {
    private val _doctorListLiveData = MutableLiveData<ArrayList<UserModel>?>()
    val doctorListLiveData: LiveData<ArrayList<UserModel>?>
        get() = _doctorListLiveData

    fun requestDoctorList() = repository.getDoctorList(_doctorListLiveData)
}