package com.company.iss.interfaces

import com.company.iss.model.AppointmentModel

interface CancelAppointmentItemClickListener {

    fun onCancelButtonClick(currentAppointment: AppointmentModel)

}