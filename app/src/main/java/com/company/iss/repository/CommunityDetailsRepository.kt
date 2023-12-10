package com.company.iss.repository

import androidx.lifecycle.MutableLiveData
import com.company.iss.model.CommunityImageModel
import com.company.iss.model.CommunityModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CommunityDetailsRepository {
    fun getCommunityDetails(title: String, liveData: MutableLiveData<CommunityModel?>) {
        Firebase.database.reference.child("facilities").child("community").child(title).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemModel = snapshot.getValue(CommunityModel::class.java)
                liveData.postValue(itemModel)
            }

            override fun onCancelled(error: DatabaseError) {
                liveData.postValue(null)
            }
        })
    }

    fun getCommunityImages(title: String, liveData: MutableLiveData<ArrayList<CommunityImageModel>?>) {
        Firebase.database.reference.child("facilities").child("community").child("images").orderByChild("title").equalTo(title).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val itemList = ArrayList<CommunityImageModel>()
                    for (dataSnapshot in snapshot.children) {
                        val itemModel = dataSnapshot.getValue(CommunityImageModel::class.java)
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