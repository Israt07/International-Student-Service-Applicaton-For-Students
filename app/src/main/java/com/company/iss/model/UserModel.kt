package com.company.iss.model

data class UserModel(
    val user_id: String? = null,
    var name: String? = null,
    var gender: String? = null,
    val matric_number: String? = null,
    val email: String? = null,
    var profile_pic_url: String? = null,
    val user_type: String? = null,
    var mobile_number: String? = null,
    val faculty: String? = null,
    val course: String? = null,
    var date_of_birth: String? = null,
    val country: String? = null,
    val passport_number: String? = null,
    val bio: String? = null,
    val specialist_in: String? = null
)