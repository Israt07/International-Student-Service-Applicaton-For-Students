package com.company.iss.repository

import androidx.lifecycle.MutableLiveData
import com.company.iss.model.FacilityImageModel
import com.company.iss.model.FacilityModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FacilityDetailsRepository {
    fun getFacilityDetails(title: String, liveData: MutableLiveData<FacilityModel?>) {
        Firebase.database.reference.child("facilities").child(title).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val facilityModel = snapshot.getValue(FacilityModel::class.java)
                liveData.postValue(facilityModel)
            }

            override fun onCancelled(error: DatabaseError) {
                liveData.postValue(null)
            }
        })
    }

    fun getFacilityImages(title: String, liveData: MutableLiveData<ArrayList<FacilityImageModel>?>) {
        Firebase.database.reference.child("facilities").child("images").orderByChild("title").equalTo(title).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val itemList = ArrayList<FacilityImageModel>()
                    for (dataSnapshot in snapshot.children) {
                        val itemModel = dataSnapshot.getValue(FacilityImageModel::class.java)
                        itemList.add(itemModel!!)
                    }
                    liveData.postValue(itemList)
                } else {
                    liveData.postValue(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                liveData.postValue(null)
            }
        })
    }
}