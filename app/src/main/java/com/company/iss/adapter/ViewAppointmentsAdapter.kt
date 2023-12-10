package com.company.iss.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.iss.R
import com.company.iss.databinding.SingleViewAppointmentItemBinding
import com.company.iss.interfaces.ViewAppointmentsItemClickListener
import com.company.iss.model.AppointmentModel

class ViewAppointmentsAdapter(private var itemList: ArrayList<AppointmentModel>, private val itemClickListener: ViewAppointmentsItemClickListener):
    RecyclerView.Adapter<ViewAppointmentsAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_view_appointment_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleViewAppointmentItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun onBind(currentItem: AppointmentModel) {
            //set details
            binding.lecturerTextview.text = currentItem.lecturer
            binding.dateTextview.text = currentItem.date

            //main item click event
            binding.viewButton.setOnClickListener {
                itemClickListener.onViewButtonClick(currentItem)
            }
        }

    }
}