package com.company.iss.model

data class PaymentModel(
    val id: String? = null,
    val user_id: String? = null,
    var fee_name: String? = null,
    val debit_rm: String? = null,
    val credit_rm: String? = null,
    val credit_date: String? = null,
    val status: String? = null
)
