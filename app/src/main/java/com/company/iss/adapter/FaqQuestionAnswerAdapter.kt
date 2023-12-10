package com.company.iss.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.iss.R
import com.company.iss.databinding.SingleFaqQuestionAnswerItemBinding
import com.company.iss.interfaces.FaqQuestionAnswerItemClickListener
import com.company.iss.model.FaqModel

class FaqQuestionAnswerAdapter(private var itemList: ArrayList<FaqModel>, private val itemClickListener: FaqQuestionAnswerItemClickListener):
    RecyclerView.Adapter<FaqQuestionAnswerAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_faq_question_answer_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleFaqQuestionAnswerItemBinding.bind(itemView)

        fun onBind(currentItem: FaqModel) {
            //set details
            binding.questionTextview.text = currentItem.question
            binding.answerTextview.text = currentItem.answer

            //question section click event
            binding.questionSection.setOnClickListener {
                itemClickListener.onQuestionButtonClick(currentItem, binding)
            }
        }

    }
}