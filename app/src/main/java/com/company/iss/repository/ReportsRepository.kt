package com.company.iss.repository

import androidx.lifecycle.MutableLiveData
import com.company.iss.model.DoctorReportModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ReportsRepository {
    fun getReportsList(userId: String, liveData: MutableLiveData<ArrayList<DoctorReportModel>?>) {
        Firebase.database.reference.child("doctors_reports").orderByChild("student_id").equalTo(userId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val itemList = ArrayList<DoctorReportModel>()
                    for (dataSnapshot in snapshot.children) {
                        val itemModel = dataSnapshot.getValue(DoctorReportModel::class.java)
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