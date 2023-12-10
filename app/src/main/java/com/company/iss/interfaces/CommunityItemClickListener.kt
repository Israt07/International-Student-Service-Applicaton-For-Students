package com.company.iss.interfaces

import com.company.iss.model.CommunityModel

interface CommunityItemClickListener {
    fun onClick(currentItem: CommunityModel)
}