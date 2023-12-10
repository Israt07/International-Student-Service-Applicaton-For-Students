package com.company.iss.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.iss.R
import com.company.iss.databinding.SingleReportItemBinding
import com.company.iss.interfaces.ReportsItemClickListener
import com.company.iss.model.DoctorReportModel

class ReportsAdapter(private var itemList: ArrayList<DoctorReportModel>, private val itemClickListener: ReportsItemClickListener):
    RecyclerView.Adapter<ReportsAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_report_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleReportItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun onBind(currentItem: DoctorReportModel) {
            //set details
            binding.doctorNameTextview.text = currentItem.doctor_name

            //main item click event
            binding.reportButton.setOnClickListener {
                itemClickListener.onItemClick(currentItem)
            }
        }

    }
}