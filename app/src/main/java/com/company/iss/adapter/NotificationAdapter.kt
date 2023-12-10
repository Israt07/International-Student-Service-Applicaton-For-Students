package com.company.iss.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.iss.R
import com.company.iss.databinding.SingleNotificationItemBinding
import com.company.iss.model.NotificationModel
import com.company.iss.utils.loadImage

class NotificationAdapter(private var itemList: ArrayList<NotificationModel>):
    RecyclerView.Adapter<NotificationAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_notification_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleNotificationItemBinding.bind(itemView)

        fun onBind(currentItem: NotificationModel) {
            //set details
            binding.notificationImageview.loadImage(currentItem.image_link)
        }

    }
}