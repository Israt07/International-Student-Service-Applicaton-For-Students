package com.company.iss.interfaces

import com.company.iss.model.HomeMenuModel

interface HomeMenuItemClickListener {
    fun onItemClick(currentItem: HomeMenuModel)
}