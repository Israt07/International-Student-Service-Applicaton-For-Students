package com.company.iss.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.iss.R
import com.company.iss.databinding.SingleDoctorItemBinding
import com.company.iss.interfaces.DoctorItemClickListener
import com.company.iss.model.UserModel

class DoctorAdapter(private var itemList: ArrayList<UserModel>, private val itemClickListener: DoctorItemClickListener):
    RecyclerView.Adapter<DoctorAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_doctor_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleDoctorItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun onBind(currentItem: UserModel) {
            //set details
            binding.nameTextview.text = "Name: ${currentItem.name}"
            binding.specialistInTextview.text = "Specialist In: ${currentItem.specialist_in}"

            //main item click event
            itemView.setOnClickListener {
                itemClickListener.onItemClick(currentItem)
            }
        }

    }
}