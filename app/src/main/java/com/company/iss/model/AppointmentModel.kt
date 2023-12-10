package com.company.iss.model

data class AppointmentModel(
    val appointment_id: String? = null,
    val user_id: String? = null,
    var name: String? = null,
    val matric_number: String? = null,
    val email: String? = null,
    val faculty: String? = null,
    val lecturer: String? = null,
    var date: String? = null,
    var time: String? = null,
    val appointment_status: String? = null,
    val rejection_reason: String? = null
)
