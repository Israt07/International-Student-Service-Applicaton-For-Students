package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.AppointmentModel
import com.company.iss.repository.CancelAppointmentRepository

class CancelAppointmentViewModel(private val repository: CancelAppointmentRepository) : ViewModel() {
    private val _pendingAppointmentListLiveData = MutableLiveData<ArrayList<AppointmentModel>?>()
    val pendingAppointmentListLiveData: LiveData<ArrayList<AppointmentModel>?>
        get() = _pendingAppointmentListLiveData

    fun requestPendingAppointmentsList(userId: String) {
        repository.getPendingAppointmentsList(userId, _pendingAppointmentListLiveData)
    }
}