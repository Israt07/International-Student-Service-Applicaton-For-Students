package com.company.iss.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.iss.R
import com.company.iss.databinding.SingleCancelAppointmentItemBinding
import com.company.iss.interfaces.CancelAppointmentItemClickListener
import com.company.iss.model.AppointmentModel

class CancelAppointmentAdapter(private var itemList: ArrayList<AppointmentModel>, private val itemClickListener: CancelAppointmentItemClickListener):
    RecyclerView.Adapter<CancelAppointmentAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_cancel_appointment_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleCancelAppointmentItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun onBind(currentItem: AppointmentModel) {
            //set details
            binding.lecturerTextview.text = currentItem.lecturer
            binding.dateTextview.text = currentItem.date

            //cancel button click event
            binding.cancelButton.setOnClickListener {
                itemClickListener.onCancelButtonClick(currentItem)
            }
        }

    }
}