package com.company.iss.interfaces

import com.company.iss.model.AppointmentModel

interface ViewAppointmentsItemClickListener {

    fun onViewButtonClick(currentAppointment: AppointmentModel)

}