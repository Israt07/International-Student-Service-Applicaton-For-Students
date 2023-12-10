package com.company.iss.model

data class HousingBookingModel(
    val booking_id: String? = null,
    val user_id: String? = null,
    var name: String? = null,
    var gender: String? = null,
    var race: String? = null,
    val matric_number: String? = null,
    val email: String? = null,
    val faculty: String? = null,
    val course: String? = null,
    val year: String? = null,
    val housing_type: String? = null,
    val duration_of_stay: String? = null,
    val booking_status: String? = null,
    val payment_status: String? = null,
    val note: String? = null
)
