package com.company.iss.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.iss.R
import com.company.iss.databinding.SingleViewHousingBookingItemBinding
import com.company.iss.interfaces.ViewBookingsItemClickListener
import com.company.iss.model.HousingBookingModel

class ViewBookingsAdapter(private var itemList: ArrayList<HousingBookingModel>, private val itemClickListener: ViewBookingsItemClickListener):
    RecyclerView.Adapter<ViewBookingsAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_view_housing_booking_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleViewHousingBookingItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun onBind(currentItem: HousingBookingModel) {
            //set details
            binding.housingTypeTextview.text = currentItem.housing_type
            binding.durationTextview.text = currentItem.duration_of_stay

            //view button click event
            binding.viewButton.setOnClickListener {
                itemClickListener.onViewButtonClick(currentItem)
            }
        }

    }
}