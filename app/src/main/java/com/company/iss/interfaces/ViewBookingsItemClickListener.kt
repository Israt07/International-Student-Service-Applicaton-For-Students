package com.company.iss.interfaces

import com.company.iss.model.HousingBookingModel

interface ViewBookingsItemClickListener {
    fun onViewButtonClick(currentItem: HousingBookingModel)
}