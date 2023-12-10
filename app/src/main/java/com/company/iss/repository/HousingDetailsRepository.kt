package com.company.iss.repository

import androidx.lifecycle.MutableLiveData
import com.company.iss.model.HousingImageModel
import com.company.iss.model.HousingModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HousingDetailsRepository {
    fun getHousingDetails(title: String, liveData: MutableLiveData<HousingModel?>) {
        Firebase.database.reference.child("services").child("housing").child(title)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val housingModel = snapshot.getValue(HousingModel::class.java)
                    liveData.postValue(housingModel)
                }

                override fun onCancelled(error: DatabaseError) {
                    liveData.postValue(null)
                }
            })
    }

    fun getHousingImages(title: String, liveData: MutableLiveData<ArrayList<HousingImageModel>?>) {
        Firebase.database.reference.child("services").child("housing").child("images").orderByChild("title").equalTo(title).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val itemList = ArrayList<HousingImageModel>()
                    for (dataSnapshot in snapshot.children) {
                        val itemModel = dataSnapshot.getValue(HousingImageModel::class.java)
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