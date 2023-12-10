package com.company.iss.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.iss.R
import com.company.iss.databinding.SingleChatItemBinding
import com.company.iss.model.ChatModel

class ChatAdapter(private var itemList: ArrayList<ChatModel>): RecyclerView.Adapter<ChatAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_chat_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleChatItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun onBind(currentItem: ChatModel) {
            binding.doctorsMessageTextview.text = currentItem.message
            binding.studentMessageTextview.text = currentItem.message

            if (currentItem.sender_id == currentItem.student_id) {
                binding.doctorsMessageTextview.visibility = View.GONE
                binding.studentMessageTextview.visibility = View.VISIBLE
            } else {
                binding.studentMessageTextview.visibility = View.GONE
                binding.doctorsMessageTextview.visibility = View.VISIBLE
            }
        }

    }
}