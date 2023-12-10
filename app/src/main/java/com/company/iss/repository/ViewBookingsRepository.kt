package com.company.iss.repository

import androidx.lifecycle.MutableLiveData
import com.company.iss.model.HousingBookingModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewBookingsRepository {
    fun getBookingList(userId: String, liveData: MutableLiveData<ArrayList<HousingBookingModel>?>) {
        Firebase.database.reference.child("services").child("housing").child("bookings").orderByChild("user_id").equalTo(userId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val itemList = ArrayList<HousingBookingModel>()
                    for (dataSnapshot in snapshot.children) {
                        val item = dataSnapshot.getValue(HousingBookingModel::class.java)
                        itemList.add(item!!)
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