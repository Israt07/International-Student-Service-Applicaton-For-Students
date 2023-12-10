package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.HandbookModel
import com.company.iss.repository.HandbookRepository

class HandbookViewModel(private val repository: HandbookRepository) : ViewModel() {
    private val _handbookListLiveData = MutableLiveData<ArrayList<HandbookModel>?>()
    val handbookListLiveData: LiveData<ArrayList<HandbookModel>?>
        get() = _handbookListLiveData

    fun requestHandbookList(faculty: String) {
        repository.getHandbookList(faculty, _handbookListLiveData)
    }
}