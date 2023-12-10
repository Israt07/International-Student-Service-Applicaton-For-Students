package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.UserModel
import com.company.iss.repository.MakeAppointmentRepository

class MakeAppointmentViewModel(private val repository: MakeAppointmentRepository) : ViewModel() {
    private val _lecturerListLiveData = MutableLiveData<ArrayList<UserModel>?>()
    val lecturerListLiveData: LiveData<ArrayList<UserModel>?>
        get() = _lecturerListLiveData

    fun requestLecturerList() = repository.getLecturerList(_lecturerListLiveData)
}