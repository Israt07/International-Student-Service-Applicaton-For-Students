package com.company.iss.interfaces

import com.company.iss.databinding.SingleFaqQuestionAnswerItemBinding
import com.company.iss.model.FaqModel

interface FaqQuestionAnswerItemClickListener {
    fun onQuestionButtonClick(currentItem: FaqModel, adapterBinding: SingleFaqQuestionAnswerItemBinding)
}