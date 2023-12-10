package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.DoctorReportModel
import com.company.iss.repository.ReportsRepository

class ReportsViewModel(private val repository: ReportsRepository) : ViewModel() {
    private val _reportListLiveData = MutableLiveData<ArrayList<DoctorReportModel>?>()
    val reportListLiveData: LiveData<ArrayList<DoctorReportModel>?>
        get() = _reportListLiveData

    fun requestReportsList(userId: String) = repository.getReportsList(userId, _reportListLiveData)
}