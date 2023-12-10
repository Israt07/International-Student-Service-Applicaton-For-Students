package com.company.iss.interfaces

import com.company.iss.model.HandbookModel

interface HandbookItemClickListener {
    fun onHandbookButtonClick(currentItem: HandbookModel)
}