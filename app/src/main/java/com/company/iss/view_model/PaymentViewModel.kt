package com.company.iss.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.iss.model.PaymentModel
import com.company.iss.repository.PaymentRepository

class PaymentViewModel(private val repository: PaymentRepository) : ViewModel() {
    private val _paymentDetailsLiveData = MutableLiveData<ArrayList<PaymentModel>?>()
    val paymentDetailsLiveData: LiveData<ArrayList<PaymentModel>?>
        get() = _paymentDetailsLiveData

    fun requestPaymentDetails(userId: String) {
        repository.getPaymentDetails(userId, _paymentDetailsLiveData)
    }
}