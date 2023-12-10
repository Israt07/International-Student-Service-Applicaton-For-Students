package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.AppointmentModel
import com.company.iss.model.HousingBookingModel
import com.company.iss.repository.ViewAppointmentsRepository
import com.company.iss.repository.ViewBookingsRepository

class ViewBookingsViewModel(private val repository: ViewBookingsRepository) : ViewModel() {
    private val _bookingListLiveData = MutableLiveData<ArrayList<HousingBookingModel>?>()
    val bookingListLiveData: LiveData<ArrayList<HousingBookingModel>?>
        get() = _bookingListLiveData

    fun requestBookingList(userId: String) {
        repository.getBookingList(userId, _bookingListLiveData)
    }
}