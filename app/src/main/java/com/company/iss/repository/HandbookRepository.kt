package com.company.iss.repository

import androidx.lifecycle.MutableLiveData
import com.company.iss.model.HandbookModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HandbookRepository {
    fun getHandbookList(faculty: String, liveData: MutableLiveData<ArrayList<HandbookModel>?>) {
        Firebase.database.reference.child("faq").child("handbook").orderByChild("faculty").equalTo(faculty).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val itemList = ArrayList<HandbookModel>()
                    for (dataSnapshot in snapshot.children) {
                        val itemModel = dataSnapshot.getValue(HandbookModel::class.java)
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