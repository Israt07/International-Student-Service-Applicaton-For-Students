package com.company.iss.repository

import androidx.lifecycle.MutableLiveData
import com.company.iss.model.AppointmentModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CancelAppointmentRepository {
    fun getPendingAppointmentsList(userId: String, liveData: MutableLiveData<ArrayList<AppointmentModel>?>) {
        Firebase.database.reference.child("appointments").orderByChild("user_id").equalTo(userId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val appointmentList = ArrayList<AppointmentModel>()
                    for (dataSnapshot in snapshot.children) {
                        val appointmentModel = dataSnapshot.getValue(AppointmentModel::class.java)
                        appointmentList.add(appointmentModel!!)
                    }
                    liveData.postValue(appointmentList)
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