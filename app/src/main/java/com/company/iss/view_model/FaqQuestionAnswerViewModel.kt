package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.FaqModel
import com.company.iss.repository.FaqQuestionAnswerRepository

class FaqQuestionAnswerViewModel(private val repository: FaqQuestionAnswerRepository) : ViewModel() {
    private val _faqListLiveData = MutableLiveData<ArrayList<FaqModel>?>()
    val faqListLiveData: LiveData<ArrayList<FaqModel>?>
        get() = _faqListLiveData

    fun requestFaqList(title: String) {
        repository.getFaqList(title, _faqListLiveData)
    }
}