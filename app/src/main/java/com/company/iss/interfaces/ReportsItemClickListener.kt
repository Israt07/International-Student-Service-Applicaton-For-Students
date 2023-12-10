package com.company.iss.interfaces

import com.company.iss.model.DoctorReportModel

interface ReportsItemClickListener {
    fun onItemClick(currentItem: DoctorReportModel)
}