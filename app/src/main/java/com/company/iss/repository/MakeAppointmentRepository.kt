package com.company.iss.repository

import androidx.lifecycle.MutableLiveData
import com.company.iss.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MakeAppointmentRepository {
    fun getLecturerList(liveData: MutableLiveData<ArrayList<UserModel>?>) {
        Firebase.database.reference.child("users").orderByChild("user_type").equalTo("LECTURER").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userList = ArrayList<UserModel>()
                    for (dataSnapshot in snapshot.children) {
                        val userModel = dataSnapshot.getValue(UserModel::class.java)
                        userList.add(userModel!!)
                    }
                    liveData.postValue(userList)
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