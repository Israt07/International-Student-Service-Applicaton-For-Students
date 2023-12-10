package com.company.iss.interfaces

import com.company.iss.model.UserModel

interface DoctorItemClickListener {
    fun onItemClick(currentItem: UserModel)
}