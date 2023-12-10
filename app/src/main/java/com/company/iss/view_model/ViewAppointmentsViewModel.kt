package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.AppointmentModel
import com.company.iss.repository.ViewAppointmentsRepository

class ViewAppointmentsViewModel(private val repository: ViewAppointmentsRepository) : ViewModel() {
    private val _appointmentsListLiveData = MutableLiveData<ArrayList<AppointmentModel>?>()
    val appointmentsListLiveData: LiveData<ArrayList<AppointmentModel>?>
        get() = _appointmentsListLiveData

    fun requestAppointmentsList(userId: String) {
        repository.getAppointmentsList(userId, _appointmentsListLiveData)
    }
}